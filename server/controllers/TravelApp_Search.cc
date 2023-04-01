/**
 * @file TravelApp_Search.cc
 * @author Li Jiawen (nmjbh@qq.com)
 * @brief 
 * @version 1.0
 * @date 2023-04-01
 * 
 * @copyright Copyright (c) 2023
 * 
 */
#include "TravelApp_Search.h"
#include "Region.h"
#include "Spot.h"

using namespace TravelApp;
using namespace drogon::orm;
using namespace drogon_model::travelapp;


// Add definition of your processing function here
void Search::searchByRegion(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                            const std::string &arg) {
    LOG_INFO << "Search by region";
    // write your application logic here
    Json::Value ret;
    auto db = drogon::app().getDbClient("Aliyun");
    auto f = db->execSqlAsyncFuture("SELECT * FROM region WHERE rname LIKE \'%" + arg + "%\';");
    try {
        auto result = f.get();
        Json::Value temp;
        for (const auto &row: result) {
            auto r = drogon_model::travelapp::Region(row);
            ret.append(r.toJson());
        }
    }
    catch (const DrogonDbException &e) {
        LOG_INFO << e.base().what();
    }
    auto resp = HttpResponse::newHttpJsonResponse(ret);
    callback(resp);

}

void Search::searchBySpot(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                          const std::string &arg) {
    LOG_INFO << "Search by spot";
    // write your application logic here
    Json::Value ret;
    auto db = drogon::app().getDbClient("Aliyun");
    auto f = db->execSqlAsyncFuture(
            "SELECT * FROM spot WHERE sname LIKE \'%" + arg + "%\' OR rname LIKE \'%" + arg + "%\';");
    try {
        auto result = f.get();
        Json::Value temp;
        for (const auto &row: result) {
            auto r = drogon_model::travelapp::Spot(row);
            ret.append(r.toJson());
        }
    }
    catch (const DrogonDbException &e) {
        LOG_INFO << e.base().what();
    }
    auto resp = HttpResponse::newHttpJsonResponse(ret);
    callback(resp);
}
