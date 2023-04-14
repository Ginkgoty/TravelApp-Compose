#include "TravelApp_Note.h"
#include "Note.h"
#include "NoteDetail.h"
#include "Traveler.h"
#include "Signer.h"

#include <Poco/JWT/Token.h>
#include <Poco/JWT/Signer.h>
#include <Poco/String.h>

using namespace TravelApp;
using namespace drogon;
using namespace drogon::orm;
using namespace Poco::JWT;


// Add definition of your processing function here
void Note::getNoteList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "getNoteList Called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Note> NoteMapper(db);
    Json::Value ret;
    try {
        auto Notes = NoteMapper.findAll();
        std::sort(Notes.begin(), Notes.end(),
                  [](drogon_model::travelapp::Note &a, drogon_model::travelapp::Note &b) {
                      return a.getValueOfNid() > b.getValueOfNid();
                  });
        for (const auto &Note: Notes) {
            ret.append(Note.toJson());
        }
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (const DrogonDbException &e) {
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}

void Note::getNoteDetail(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int nid) {
    LOG_INFO << "getNoteDetail Called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::NoteDetail> NoteDetailMapper(db);
    Mapper<drogon_model::travelapp::Traveler> userMapper(db);

    Json::Value ret;
    try {
        auto detail = NoteDetailMapper.findByPrimaryKey(
                drogon_model::travelapp::NoteDetail::PrimaryKeyType{nid});
        auto author = userMapper
                .findOne(Criteria("uid", CompareOperator::EQ, detail.getValueOfUid()));
        ret = detail.toJson();

        // get user infomation
        ret.removeMember("rid");
        ret["uname"]=author.getValueOfUname();
        ret["upic"]=author.getValueOfUpic();

        std::string content = ret["content"].asString();

        Json::Reader reader;
        Json::Value root;

        if (reader.parse(content, root)) {
            ret["content"] = root;
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            callback(resp);
        } else {
            auto resp = HttpResponse::newHttpResponse();
            resp->setStatusCode(drogon::k500InternalServerError);
            callback(resp);
        }
    } catch (const DrogonDbException &e) {
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}

void Note::uploadNote(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Upload Note Called!";

    static auto nid = 30000000;
    std::mutex nid_mutex;

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");


    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Note> noteMapper(db);
    Mapper<drogon_model::travelapp::NoteDetail> noteDetailMapper(db);

    auto note = drogon_model::travelapp::Note();
    auto noteDetail = drogon_model::travelapp::NoteDetail();

    // Add mutex to protect nid
    nid_mutex.lock();
    note.setNid(nid);
    noteDetail.setNid(nid++);
    nid_mutex.unlock();

    auto content = (*json)["detail"]["content"];

    // Build Note Object
    note.setImg((*json)["detail"]["background"].asString());
    note.setTitle((*json)["detail"]["title"].asString());
    for (const auto &i: content) {
        if (i["kind"].asInt() == 1) {
            note.setIntro(i["content"].asString());
            break;
        }
    }
    note.setRname((*json)["detail"]["rname"].asString());


    // Build Note Detail Object
    // Construct WriterBuilder
    // json -> string
    Json::StreamWriterBuilder builder;
    builder["emitUTF8"] = true;

    noteDetail.setTitle((*json)["detail"]["title"].asString());
    noteDetail.setBackground((*json)["detail"]["background"].asString());
    noteDetail.setTime(trantor::Date::date());
    noteDetail.setUid(uid);
    std::string content_string{Poco::replace(writeString(builder, content), "\n", "")};
    std::string content_result{Poco::replace(content_string, "\t", "")};
    noteDetail.setContent(content_result);

    Json::Value ret;
    // Insert into DB
    try {
        noteMapper.insert(note);
        noteDetailMapper.insert(noteDetail);
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

