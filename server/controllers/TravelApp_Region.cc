#include "TravelApp_Region.h"
#include "Spot.h"
#include "Region.h"
#include "Food.h"

using namespace TravelApp;
using namespace drogon::orm;
using namespace drogon;

// Add definition of your processing function here
void Region::get(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid) {
    LOG_INFO << "Get spots list of region " << rid;
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Spot> spotMapper(db);

    std::string sql = "UPDATE region SET view=view+1 WHERE rid = $1";

    db->execSqlAsync(
            sql,
            [](const Result &r) {

            },
            [](const DrogonDbException &e) {
                LOG_INFO << e.base().what();
            },
            rid
    );

    spotMapper.findBy(
            Criteria("rid", CompareOperator::EQ, rid),
            [callback](const std::vector<drogon_model::travelapp::Spot> &spots) {
                Json::Value ret;
                for (auto &spot: spots) {
                    ret.append(spot.toJson());
                }
                auto resp = HttpResponse::newHttpJsonResponse(ret);
                callback(resp);
            },
            [callback](const DrogonDbException &e) {
                LOG_ERROR << "rid NOT EXISTS!";
            }
    );
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

void Region::getFoodList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid) {
    LOG_INFO << "Get food info" << rid;
    auto db = drogon::app().getDbClient("Aliyun");
    std::string sql = "SELECT * "
                      "FROM food "
                      "WHERE rid = $1";

    db->execSqlAsync(sql,
                     [callback](const Result &foods) {
                         Json::Value ret;
                         for (const auto &food: foods) {
                             ret.append(drogon_model::travelapp::Food(food).toJson());
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
                     rid);

}
