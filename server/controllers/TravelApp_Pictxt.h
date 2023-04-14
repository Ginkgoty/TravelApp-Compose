#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class Pictxt : public drogon::HttpController<Pictxt> {
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(Pictxt::uploadPictxt, "/pictxt/upload", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Pictxt::getPictxt, "/pictxt", Post);
            ADD_METHOD_TO(Pictxt::favoritePictxt, "/pictxt/fav?ptid={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Pictxt::unfavoritePictxt, "/pictxt/unfav?ptid={}", Post, "TravelApp::LoginFilter");
        METHOD_LIST_END

        void uploadPictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        void getPictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        void
        favoritePictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int ptid);

        void
        unfavoritePictxt(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int ptid);
    };
}
