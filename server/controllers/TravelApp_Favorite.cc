#include "TravelApp_Favorite.h"
#include "Favorspots.h"
#include "Favorregions.h"
#include "Signer.h"
#include "Spot.h"
#include "Region.h"

#include <Poco/JWT/Token.h>
#include <Poco/JWT/Signer.h>

using namespace TravelApp;
using namespace drogon;
using namespace drogon::orm;
using namespace Poco::JWT;

// Add definition of your processing function here
void Favorite::favoriteSpot(const HttpRequestPtr &req,
                            std::function<void(const HttpResponsePtr &)> &&callback,
                            int sid) {
    LOG_INFO << "favoriteSpots Called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorspots> favoriteMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    auto f = drogon_model::travelapp::Favorspots();
    f.setUid(uid);
    f.setSid(sid);

    Json::Value ret;
    try {
        favoriteMapper.insert(f);
        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(k500InternalServerError);
        callback(resp);
    }
}

void Favorite::favoriteRegion(const HttpRequestPtr &req,
                              std::function<void(const HttpResponsePtr &)> &&callback,
                              int rid) {
    LOG_INFO << "favoriteRegions Called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorregions> favoriteMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    auto f = drogon_model::travelapp::Favorregions();
    f.setUid(uid);
    f.setRid(rid);

    Json::Value ret;
    try {
        favoriteMapper.insert(f);
        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(k500InternalServerError);
        callback(resp);
    }
}

void Favorite::unfavoriteSpot(const HttpRequestPtr &req,
                              std::function<void(const HttpResponsePtr &)> &&callback,
                              int sid) {
    LOG_INFO << "unfavoriteSpots Called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorspots> favoriteMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    Json::Value ret;
    try {
        if (favoriteMapper
                .deleteByPrimaryKey(drogon_model::travelapp::Favorspots::PrimaryKeyType{uid, sid})) {
            ret["result"] = true;
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            callback(resp);
        } else {
            ret["result"] = false;
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            resp->setStatusCode(k500InternalServerError);
            callback(resp);
        }
    } catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(k500InternalServerError);
        callback(resp);
    }

}

void Favorite::unfavoriteRegion(const HttpRequestPtr &req,
                                std::function<void(const HttpResponsePtr &)> &&callback,
                                int rid) {
    LOG_INFO << "unfavoriteRegions Called!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorregions> favoriteMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    Json::Value ret;
    try {
        if (favoriteMapper
                .deleteByPrimaryKey(drogon_model::travelapp::Favorregions::PrimaryKeyType{uid, rid})) {
            ret["result"] = true;
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            callback(resp);
        } else {
            ret["result"] = false;
            auto resp = HttpResponse::newHttpJsonResponse(ret);
            resp->setStatusCode(k500InternalServerError);
            callback(resp);
        }

    } catch (DrogonDbException &e) {
        LOG_INFO << e.base().what();
        ret["result"] = "Unfavorite failed!";
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(k500InternalServerError);
        callback(resp);
    }

}

void Favorite::getFavoriteSpots(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Get Favorite Spots!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorspots> favoriteMapper(db);
    Mapper<drogon_model::travelapp::Spot> spotMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    Json::Value ret;
    try {
        auto result = favoriteMapper
                .findBy(Criteria("uid", CompareOperator::EQ, uid));
        for (const auto &r: result) {
            auto spot = spotMapper.findByPrimaryKey(drogon_model::travelapp::Spot::PrimaryKeyType{r.getValueOfSid()});
            ret.append(spot.toJson());
        }
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (DrogonDbException &e) {
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k404NotFound);
        callback(resp);
    }
}

void Favorite::getFavoriteRegions(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Get Favorite Regions!";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorregions> favoriteMapper(db);
    Mapper<drogon_model::travelapp::Region> regionMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    Json::Value ret;
    try {
        auto result = favoriteMapper
                .findBy(Criteria("uid", CompareOperator::EQ, uid));
        for (const auto &r: result) {
            auto region = regionMapper
                    .findByPrimaryKey(drogon_model::travelapp::Spot::PrimaryKeyType{r.getValueOfRid()});
            ret.append(region.toJson());
        }
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (DrogonDbException &e) {
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k404NotFound);
        callback(resp);
    }
}

void
Favorite::checkRegionFavoriteStatus(const HttpRequestPtr &req,
                                    std::function<void(const HttpResponsePtr &)> &&callback,
                                    int rid) {
    LOG_INFO << "Check Favorite Region";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorregions> favoriteMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    Json::Value ret;
    try {
        auto result = favoriteMapper.findByPrimaryKey(
                drogon_model::travelapp::Favorregions::PrimaryKeyType{uid, rid});
        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (DrogonDbException &e) {
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
}

void
Favorite::checkSpotFavoriteStatus(const HttpRequestPtr &req,
                                  std::function<void(const HttpResponsePtr &)> &&callback,
                                  int sid) {
    LOG_INFO << "Check Favorite Spot";
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Favorspots> favoriteMapper(db);

    auto json = req->getJsonObject();
    std::string jwt = (*json)["token"].asString();
    Signer signer(SIGN_KEY);
    Token token = signer.verify(jwt);
    int uid = token.payload().get("uid");

    Json::Value ret;
    try {
        auto result = favoriteMapper.findByPrimaryKey(
                drogon_model::travelapp::Favorspots::PrimaryKeyType{uid, sid});
        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (DrogonDbException &e) {
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }

}
