#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class Region : public drogon::HttpController<Region> {
    public:
        METHOD_LIST_BEGIN
            // use METHOD_ADD to add your custom processing function here;
            // METHOD_ADD(Region::get, "/{2}/{1}", Get); // path is /TravelApp/Region/{arg2}/{arg1}
            // METHOD_ADD(Region::your_method_name, "/{1}/{2}/list", Get); // path is /TravelApp/Region/{arg1}/{arg2}/list
            ADD_METHOD_TO(Region::get, "/region?rid={}", Get);
            ADD_METHOD_TO(Region::getRegionInfo, "/region/info?rid={}", Get);
            ADD_METHOD_TO(Region::getFoodList, "/food?rid={}", Get);
            // ADD_METHOD_TO(Region::your_method_name, "/absolute/path/{1}/{2}/list", Get); // path is /absolute/path/{arg1}/{arg2}/list
        METHOD_LIST_END

        // your declaration of processing function maybe like this:
        void get(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid);

        void getRegionInfo(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid);

        void getFoodList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int rid);
        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
    };
}
