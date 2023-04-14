#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class Search : public drogon::HttpController<Search> {
    public:
        METHOD_LIST_BEGIN
            // use METHOD_ADD to add your custom processing function here;
            // METHOD_ADD(Search::get, "/{2}/{1}", Get); // path is /TravelApp/Search/{arg2}/{arg1}
            // METHOD_ADD(Search::your_method_name, "/{1}/{2}/list", Get); // path is /TravelApp/Search/{arg1}/{arg2}/list
            ADD_METHOD_TO(Search::searchByRegion, "/search/region?keyword={}", Get);
            ADD_METHOD_TO(Search::searchBySpot, "/search/spot?keyword={}", Get);
            // ADD_METHOD_TO(Search::your_method_name, "/absolute/path/{1}/{2}/list", Get); // path is /absolute/path/{arg1}/{arg2}/list
        METHOD_LIST_END

        // your declaration of processing function maybe like this:
        void searchByRegion(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                            const std::string &arg);

        void searchBySpot(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                          const std::string &arg);
        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
    };
}
