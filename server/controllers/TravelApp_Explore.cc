/**
 * @file TravelApp_Explore.cc
 * @author Li Jiawen (nmjbh@qq.com)
 * @brief 
 * @version 1.0
 * @date 2023-04-01
 * 
 * @copyright Copyright (c) 2023
 * 
 */
#include "TravelApp_Explore.h"
#include "models/Region.h"

using namespace TravelApp;
using namespace drogon::orm;
using namespace drogon_model::travelapp;

void Main::asyncHandleHttpRequest(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "Main View Request!";
    // write your application logic here
    Json::Value ret;
    auto db = drogon::app().getDbClient("Aliyun");

    Mapper<drogon_model::travelapp::Region> regionMapper(db);
    try {
        auto result = regionMapper.findAll();
        int i = 0;
        for (const auto &r: result) {
            ret.append(r.toJson());
            ++i;
            if (i == 20)  // only use top 20 regions
                break;
        }
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } catch (const DrogonDbException &e) {
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}
