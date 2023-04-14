#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class Main : public drogon::HttpController<Main> {
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(Main::getRegionList, "/main", Get);
            ADD_METHOD_TO(Main::getRecommendList, "/recommend", Get);
        METHOD_LIST_END

        void getRegionList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        void getRecommendList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);
    };
}
