#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class Spot : public drogon::HttpController<Spot> {
    public:
        METHOD_LIST_BEGIN
            // use METHOD_ADD to add your custom processing function here;
            // METHOD_ADD(Spot::get, "/{2}/{1}", Get); // path is /TravelApp/Spot/{arg2}/{arg1}
            // METHOD_ADD(Spot::your_method_name, "/{1}/{2}/list", Get); // path is /TravelApp/Spot/{arg1}/{arg2}/list
            // ADD_METHOD_TO(Spot::your_method_name, "/absolute/path/{1}/{2}/list", Get); // path is /absolute/path/{arg1}/{arg2}/list
            ADD_METHOD_TO(Spot::get, "/spot?sid={}", Get);
        METHOD_LIST_END

        // your declaration of processing function maybe like this:
        void get(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, int sid);
        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
    };
}
