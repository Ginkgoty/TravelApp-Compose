#include "TravelApp_User.h"
#include "Traveler.h"
#include "Signer.h"

#include <Poco/DigestStream.h>
#include <Poco/SHA1Engine.h>
#include <Poco/StreamCopier.h>
#include <Poco/JWT/Token.h>
#include <Poco/JWT/Signer.h>

using namespace drogon;
using namespace drogon::orm;
using namespace TravelApp;
using namespace Poco::JWT;

std::string SHA1(const std::string &origin) {
    Poco::SHA1Engine sha1;
    Poco::DigestOutputStream ds(sha1);
    ds << origin;
    ds.close();
    return Poco::DigestEngine::digestToHex(sha1.digest());
}

std::string generateToken(const int uid, const std::string &uname) {
    Token token;
    token.setType("JWT");
    token.setSubject("User Info");
    token.payload().set("uid", uid).set("uname", uname);
    token.setIssuedAt(Poco::Timestamp());

    Signer signer(SIGN_KEY);
    std::string jwt = signer.sign(token, Signer::ALGO_HS256);
    return jwt;
}

void User::sign_up(const HttpRequestPtr &req,
                   std::function<void(const HttpResponsePtr &)> &&callback,
                   const std::string &uname,
                   const std::string &pwd) {
    LOG_INFO << "Sign up called!";

    static int uid = 10000000;
    std::mutex u_mutex;

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Traveler> userMapper(db);
    try {
        auto result = userMapper.findOne(Criteria("uname", CompareOperator::EQ, uname));
    }
    catch (DrogonDbException &e) {
        Json::Value ret;
        LOG_INFO << e.base().what();
        auto user = drogon_model::travelapp::Traveler();
        u_mutex.lock();
        user.setUid(uid);
        ret["token"] = generateToken(uid, uname);
        uid += 1;
        u_mutex.unlock();
        user.setUname(uname);
        user.setPwd(SHA1(pwd));
        user.setUpic("https://118.31.67.238/gallary/default.jpg");
        userMapper.insert(user);
        ret["result"] = "sign-up success!";
        ret["upic"] = "https://118.31.67.238/gallary/default.jpg";
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    Json::Value ret;
    ret["result"] = "Duplicate username";
    auto resp = HttpResponse::newHttpJsonResponse(ret);
    resp->setStatusCode(k403Forbidden);
    callback(resp);
}

void User::sign_in(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                   const std::string &uname, const std::string &pwd) {
    LOG_INFO << "Sign in called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Traveler> userMapper(db);
    drogon_model::travelapp::Traveler result;
    Json::Value ret;
    try {
        result = userMapper.findOne(Criteria("uname", CompareOperator::EQ, uname));
        if (result.getValueOfPwd() == SHA1(pwd)) {
            ret["result"] = "Sign-in success!";
            ret["token"] = generateToken(result.getValueOfUid(), uname);
            ret["upic"] = result.getValueOfUpic();
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            callback(resp);
        } else {
            ret["result"] = "Password incorrect!";
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            resp->setStatusCode(k401Unauthorized);
            callback(resp);
        }
    }
    catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        ret["result"] = "User does not exist！";
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(k404NotFound);
        callback(resp);
    }

}

void User::change_pwd(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                      const std::string &pwd) {
    LOG_INFO << "change pwd called!";

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Traveler> userMapper(db);
    drogon_model::travelapp::Traveler result;
    Json::Value ret;

    try {
        // paser json to get uname
        auto json = req->getJsonObject();
        std::string jwt = (*json)["token"].asString();
        Signer signer(SIGN_KEY);
        Token token = signer.verify(jwt);
        std::string uname = token.payload().get("uname");

        auto newPwd = SHA1(pwd);
        userMapper.updateBy({"pwd"},
                            Criteria("uname", CompareOperator::EQ, uname), newPwd);

        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        ret["result"] = false;
        auto resp = HttpResponse::newHttpResponse();
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}

void User::change_uname(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                        const std::string &uname) {
    LOG_INFO << "change uname called!";

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Traveler> userMapper(db);
    drogon_model::travelapp::Traveler result;
    Json::Value ret;

    try {
        // paser json to get old uname
        auto json = req->getJsonObject();
        std::string jwt = (*json)["token"].asString();
        Signer signer(SIGN_KEY);
        Token token = signer.verify(jwt);
        std::string old_uname = token.payload().get("uname");

        userMapper.updateBy({"uname"},
                            Criteria("uname", CompareOperator::EQ, old_uname), uname);

        result = userMapper.findOne(Criteria("uname", CompareOperator::EQ, uname));

        ret["token"] = generateToken(result.getValueOfUid(), uname);
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}

void User::change_upic(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                       const std::string &upic) {
    LOG_INFO << "change upic called!";

    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Traveler> userMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    userMapper.updateBy(
            {"upic"},
            [callback](const size_t size) {
                if (size != 0) {
                    Json::Value ret;
                    ret["result"] = true;
                    auto resp = HttpResponse::newHttpJsonResponse(ret);
                    callback(resp);
                }else{
                    Json::Value ret;
                    ret["result"] = false;
                    auto resp = HttpResponse::newHttpJsonResponse(ret);
                    callback(resp);
                }
            },
            [callback](const DrogonDbException &e) {
                Json::Value ret;
                ret["result"] = false;
                auto resp = HttpResponse::newHttpJsonResponse(ret);
                resp->setStatusCode(drogon::k500InternalServerError);
                callback(resp);
            },
            Criteria("uid", uid),
            upic
    );


}
