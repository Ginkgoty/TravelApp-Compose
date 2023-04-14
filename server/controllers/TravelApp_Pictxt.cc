#include "TravelApp_Pictxt.h"
#include "Signer.h"
#include "PicText.h"
#include "Favpictxt.h"

#include <Poco/JWT/Token.h>
#include <Poco/JWT/Signer.h>
#include <Poco/String.h>

using namespace TravelApp;
using namespace drogon;
using namespace drogon::orm;
using namespace Poco::JWT;


// Add definition of your processing function here
void Pictxt::uploadPictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Upload Pictxt Called!";

    static auto ptid = 10000000;
    std::mutex pt_mutex;

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);

    int uid = token.payload().get("uid");

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::PicText> pictxtMapper(db);
    auto pictxt = drogon_model::travelapp::PicText();

    Json::StreamWriterBuilder builder;
    builder["emitUTF8"] = true;

    // construct pic txt
    pt_mutex.lock();
    pictxt.setPtid(ptid++);
    pt_mutex.unlock();
    pictxt.setUid(uid);
    pictxt.setTitle((*json)["title"].asString());
    pictxt.setText((*json)["text"].asString());
    pictxt.setCover((*json)["imglist"][0].asString());
    pictxt.setImagelist(Poco::replace(
            Poco::replace(writeString(builder, (*json)["imglist"]), "\n", ""),
            "\t",
            "")
    );
    pictxt.setDatetime(trantor::Date::date());

    Json::Value ret;
    try {
        // Insert into DB
        pictxtMapper.insert(pictxt);
        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (const DrogonDbException &e) {
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}

void Pictxt::getPictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Get Pictxt Called!";

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    int uid = 0;
    if (!jwt.empty()) {
        Token token = signer.verify(jwt);
        uid = token.payload().get("uid");
    }

    auto db = drogon::app().getDbClient("Aliyun");
    Json::Value ret;

    std::string sql = R"(
        SELECT
            pt.ptid,
            pt.cover,
            pt.imagelist,
            pt.title,
            pt.text,
            pt.datetime,
            pt.favcount,
            tr.uid as author_uid,
            tr.uname,
            tr.upic,
            (CASE WHEN fp.ptid IS NULL THEN false ELSE true END) as isfavorite
        FROM
            pic_text pt
        LEFT JOIN
            traveler tr ON pt.uid = tr.uid
        LEFT JOIN
            favpictxt fp ON pt.ptid = fp.ptid AND fp.uid = $1
    )";

    db->execSqlAsync(sql, [callback](const Result &result) {
                         Json::Reader reader;
                         Json::Value jsonResponse;
                         for (const auto &row: result) {
                             Json::Value item;
                             item["ptid"] = row["ptid"].as<int>();
                             item["uname"] = row["uname"].as<std::string>();
                             item["upic"] = row["upic"].as<std::string>();
                             item["cover"] = row["cover"].as<std::string>();
                             reader.parse(row["imagelist"].as<std::string>(), item["imagelist"]);
                             item["title"] = row["title"].as<std::string>();
                             item["text"] = row["text"].as<std::string>();
                             item["datetime"] = row["datetime"].as<std::string>();
                             item["favcount"] = row["favcount"].as<int>();
                             item["isfavorite"] = row["isfavorite"].as<bool>();
                             jsonResponse.append(item);
                         }
                         auto response = HttpResponse::newHttpJsonResponse(jsonResponse);
                         callback(response);
                     },
                     [callback](const DrogonDbException &e) {
                         LOG_INFO << e.base().what();
                         Json::Value ret;
                         ret["result"] = false;
                         auto response = HttpResponse::newHttpJsonResponse(ret);
                         callback(response);
                     },
                     uid
    );

}

void
Pictxt::favoritePictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int ptid) {
    LOG_INFO << "Fav Pictxt Called!";

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favpictxt> favMapper(db);
    auto record = std::make_shared<drogon_model::travelapp::Favpictxt>(drogon_model::travelapp::Favpictxt());

    record->setUid(uid);
    record->setPtid(ptid);

    favMapper.insert(
            *record,
            [callback](const drogon_model::travelapp::Favpictxt &r) {
                LOG_INFO << r.getValueOfUid() << " " << r.getValueOfPtid();
            },
            [callback](const DrogonDbException &e) {
                LOG_INFO << e.base().what();
                Json::Value ret;
                ret["result"] = false;
                auto response = HttpResponse::newHttpJsonResponse(ret);
                response->setStatusCode(drogon::k500InternalServerError);
                callback(response);
            }
    );

    std::string sql = "UPDATE pic_text SET favcount = favcount + 1 WHERE ptid = $1;";

    db->execSqlAsync(
            sql,
            [callback](const Result &result) {
                Json::Value ret;
                ret["result"] = true;
                auto response = HttpResponse::newHttpJsonResponse(ret);
                callback(response);
            },
            [callback](const DrogonDbException &e) {
                LOG_INFO << e.base().what();
                Json::Value ret;
                ret["result"] = false;
                auto response = HttpResponse::newHttpJsonResponse(ret);
                callback(response);
            },
            ptid
    );
}

void
Pictxt::unfavoritePictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int ptid) {
    LOG_INFO << "Unfav Pictxt Called!";

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favpictxt> favMapper(db);

    favMapper.deleteByPrimaryKey(
            {uid, ptid},
            [callback](const size_t &size) {
                LOG_INFO << size;
            },
            [callback](const DrogonDbException &e) {
                LOG_INFO << e.base().what();
                Json::Value ret;
                ret["result"] = false;
                auto response = HttpResponse::newHttpJsonResponse(ret);
                response->setStatusCode(drogon::k500InternalServerError);
                callback(response);
            }
    );

    std::string sql = "UPDATE pic_text SET favcount = favcount - 1 WHERE ptid = $1;";

    db->execSqlAsync(
            sql,
            [callback](const Result &result) {
                Json::Value ret;
                ret["result"] = true;
                auto response = HttpResponse::newHttpJsonResponse(ret);
                callback(response);
            },
            [callback](const DrogonDbException &e) {
                LOG_INFO << e.base().what();
                Json::Value ret;
                ret["result"] = false;
                auto response = HttpResponse::newHttpJsonResponse(ret);
                callback(response);
            },
            ptid
    );

}


