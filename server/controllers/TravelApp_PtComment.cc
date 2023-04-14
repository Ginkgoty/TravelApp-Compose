#include "TravelApp_PtComment.h"
#include "PtComment.h"
#include "Signer.h"

#include <Poco/JWT/Token.h>
#include <Poco/JWT/Signer.h>
#include <drogon/orm/Exception.h>
#include <trantor/utils/Date.h>

using namespace TravelApp;
using namespace Poco::JWT;
using namespace drogon;
using namespace drogon::orm;

void PtComment::fetchComments(const drogon::HttpRequestPtr &req,
                              std::function<void(const drogon::HttpResponsePtr &)> &&callback, int ptid) {
    LOG_INFO << "Fetch Comments Called!";
    auto client = drogon::app().getDbClient("Aliyun");
    std::string sql = "WITH RECURSIVE all_comments AS ("
                      "SELECT pt_comment.*, traveler.* "
                      "FROM pt_comment "
                      "JOIN traveler ON pt_comment.uid = traveler.uid "
                      "WHERE ptid = $1 AND belong = 0 "
                      "UNION ALL "
                      "SELECT pt_comment.*, traveler.* "
                      "FROM pt_comment "
                      "JOIN traveler ON pt_comment.uid = traveler.uid "
                      "JOIN all_comments ac ON pt_comment.belong = ac.cid"
                      ")"
                      "SELECT * FROM all_comments;";

    client->execSqlAsync(
            sql,
            [callback](const drogon::orm::Result &result) {
                std::vector<std::shared_ptr<Comment>> comments;
                for (const auto &row: result) {
                    drogon_model::travelapp::PtComment ptComment(row, -1);
                    drogon_model::travelapp::Traveler traveler(row, -1);

                    auto comment = std::make_shared<Comment>(ptComment, traveler);
                    comments.emplace_back(comment);
                }

                auto commentsTree = buildCommentsTree(comments);

                Json::Value jsonComments(Json::arrayValue);
                for (const auto &comment: commentsTree) {
                    jsonComments.append(commentToJson(comment));
                }

                auto resp = drogon::HttpResponse::newHttpJsonResponse(jsonComments);
                callback(resp);
            },
            [callback](const drogon::orm::DrogonDbException &e) {
                auto resp = drogon::HttpResponse::newHttpResponse();
                resp->setStatusCode(drogon::k500InternalServerError);
                resp->setContentTypeCode(drogon::CT_TEXT_PLAIN);
                resp->setBody("Error: " + std::string(e.base().what()));
                callback(resp);
            },
            ptid);
}

void PtComment::comment(const HttpRequestPtr &req, std::function<void(const drogon::HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Comment Called!";

    static auto cid = 20000000;
    std::mutex c_mutex;

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);

    int uid = token.payload().get("uid");

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::PtComment> ptCommentMapper(db);
    auto ptComment = drogon_model::travelapp::PtComment();
    c_mutex.lock();
    ptComment.setCid(cid++);
    c_mutex.unlock();
    ptComment.setPtid((*json)["ptid"].asInt());
    ptComment.setUid(uid);
    ptComment.setBelong((*json)["belong"].asInt());
    ptComment.setComment((*json)["comment"].asString());
    ptComment.setDatetime(trantor::Date::date());

    Json::Value ret;
    try {
        ptCommentMapper.insert(ptComment);
        ret["result"] = true;
        auto resp = drogon::HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (const DrogonDbException &e) {
        auto resp = drogon::HttpResponse::newHttpResponse();
        resp->setStatusCode(drogon::k500InternalServerError);
        resp->setContentTypeCode(drogon::CT_TEXT_PLAIN);
        resp->setBody("Error: " + std::string(e.base().what()));
        callback(resp);
    }
}
