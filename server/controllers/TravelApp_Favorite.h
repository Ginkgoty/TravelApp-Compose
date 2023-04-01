/**
 * @file TravelApp_Favorite.h
 * @author Li Jiawen (nmjbh@qq.com)
 * @brief 
 * @version 1.0
 * @date 2023-04-01
 * 
 * @copyright Copyright (c) 2023
 * 
 */
#pragma once

#include <drogon/HttpController.h>
#include <drogon/HttpFilter.h>

using namespace drogon;

namespace TravelApp {
    class Favorite : public drogon::HttpController<Favorite> {
    public:
        METHOD_LIST_BEGIN
            // use METHOD_ADD to add your custom processing function here;
            // METHOD_ADD(Favorite::get, "/{2}/{1}", Get); // path is /TravelApp/Favorite/{arg2}/{arg1}
            // METHOD_ADD(Favorite::your_method_name, "/{1}/{2}/list", Get); // path is /TravelApp/Favorite/{arg1}/{arg2}/list
            // ADD_METHOD_TO(Favorite::your_method_name, "/absolute/path/{1}/{2}/list", Get); // path is /absolute/path/{arg1}/{arg2}/list
            ADD_METHOD_TO(Favorite::favoriteSpot, "/favspot?sid={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::favoriteRegion, "/favregion?rid={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::unfavoriteSpot, "/favspot/delete?sid={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::unfavoriteRegion, "/favregion/delete?rid={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::getFavoriteSpots, "/favspot/get", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::getFavoriteRegions, "/favregion/get", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::checkSpotFavoriteStatus, "/favspot/check?sid={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(Favorite::checkRegionFavoriteStatus, "/favregion/check?rid={}", Post, "TravelApp::LoginFilter");
        METHOD_LIST_END


        // your declaration of processing function maybe like this:
        void favoriteSpot(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int sid);

        void favoriteRegion(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                            int rid);

        void unfavoriteSpot(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                            int sid);

        void unfavoriteRegion(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                              int rid);

        void getFavoriteSpots(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        void getFavoriteRegions(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        void checkRegionFavoriteStatus(const HttpRequestPtr &req,
                                       std::function<void(const HttpResponsePtr &)> &&callback,
                                       int rid);

        void checkSpotFavoriteStatus(const HttpRequestPtr &req,
                                     std::function<void(const HttpResponsePtr &)> &&callback,
                                     int sid
                                     );
        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
    };
}
