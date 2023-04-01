/**
 * @file TravelApp_Region.cc
 * @author Li Jiawen (nmjbh@qq.com)
 * @brief 
 * @version 1.0
 * @date 2023-04-01
 * 
 * @copyright Copyright (c) 2023
 * 
 */
#include "TravelApp_Region.h"
#include "Spot.h"
#include "Region.h"

using namespace TravelApp;
using namespace drogon::orm;
using namespace drogon;

// Add definition of your processing function here
void Region::get(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid) {
    LOG_INFO << "Get spots list of region " << rid;
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Spot> spotMapper(db);
    try {
        auto spots = spotMapper.findBy(Criteria("rid", CompareOperator::EQ, rid));
        Json::Value ret;
        for (const auto &spot: spots) {
            ret.append(spot.toJson());
        }
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_ERROR << "rid NOT EXISTS!";
    }
}

void
Region::getRegionInfo(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid) {
    LOG_INFO << "Get region info" << rid;
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Region> regionMapper(db);
    try {
        auto region = regionMapper.findByPrimaryKey(drogon_model::travelapp::Region::PrimaryKeyType{rid});
        auto resp = HttpResponse::newHttpJsonResponse(region.toJson());
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_ERROR << "rid NOT EXISTS!";
    }

}
