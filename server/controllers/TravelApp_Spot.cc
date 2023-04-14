#include "TravelApp_Spot.h"
#include "Detail.h"

using namespace TravelApp;
using namespace drogon_model::travelapp;
using namespace drogon::orm;
using namespace drogon;

// Add definition of your processing function here
void Spot::get(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int sid) {
    LOG_INFO << "Get spots detial of spot " << sid;
    auto db = drogon::app().getDbClient("Aliyun");
    Mapper<drogon_model::travelapp::Detail> detailMapper(db);
    try {
        auto result = detailMapper.findByPrimaryKey(sid);
        Json::Value ret = result.toJson();
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    }
    catch (DrogonDbException &e) {
        LOG_ERROR << "sid NOT EXISTS!";
    }
}
