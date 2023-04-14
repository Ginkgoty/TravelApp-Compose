#include "TravelApp_Explore.h"
#include "models/Region.h"

using namespace TravelApp;
using namespace drogon::orm;
using namespace drogon_model::travelapp;

void Main::getRegionList(const drogon::HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Main View Request!";
    // write your application logic here

    auto db = drogon::app().getDbClient("Aliyun");

    std::string sql_string = "SELECT * "
                             "FROM region "
                             "ORDER BY view DESC "
                             "LIMIT 20;";
    db->execSqlAsync(
            sql_string,
            [callback](const Result &result) {
                Json::Value ret;
                for (const auto &record: result) {
                    ret.append(Region(record).toJson());
                }
                auto resp = HttpResponse::newHttpJsonResponse(ret);
                callback(resp);
            },
            [callback](const DrogonDbException &e) {
                Json::Value ret;
                auto resp = HttpResponse::newHttpJsonResponse(ret);
                resp->setStatusCode(drogon::k500InternalServerError);
                callback(resp);
            }
    );
}

void Main::getRecommendList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Main View recommend Request!";
    // write your application logic here

    auto db = drogon::app().getDbClient("Aliyun");
    auto month = std::atoi(trantor::Date::now().toCustomedFormattedStringLocal("%m").c_str());
    LOG_INFO << month;


    std::string sql_string = "SELECT r2.* "
                             "FROM recommendation r1, region r2 "
                             "WHERE r1.rid = r2.rid AND month = $1 ;";
    db->execSqlAsync(
            sql_string,
            [callback](const Result &result) {
                Json::Value ret;
                for (const auto &record: result) {
                    ret.append(Region(record).toJson());
                }
                auto resp = HttpResponse::newHttpJsonResponse(ret);
                callback(resp);
            },
            [callback](const DrogonDbException &e) {
                Json::Value ret;
                auto resp = HttpResponse::newHttpJsonResponse(ret);
                resp->setStatusCode(drogon::k500InternalServerError);
                callback(resp);
            },
            month
    );
}
