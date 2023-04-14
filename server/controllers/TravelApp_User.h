#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class User : public drogon::HttpController<User> {
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(User::sign_up, "/sign_up?uname={1}&pwd={2}", Post);
            ADD_METHOD_TO(User::sign_in, "/sign_in?uname={1}&pwd={2}", Post);
            ADD_METHOD_TO(User::change_pwd, "/change/pwd?pwd={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(User::change_uname, "/change/uname?uname={}", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(User::change_upic, "/change/upic?upic={}", Post, "TravelApp::LoginFilter");
        METHOD_LIST_END

        // your declaration of processing function maybe like this:
        void sign_up(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                     const std::string &uname,
                     const std::string &pwd);

        void sign_in(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                     const std::string &uname,
                     const std::string &pwd
        );

        void change_pwd(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                        const std::string &pwd);

        void change_uname(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                          const std::string &uname);

        void change_upic(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback,
                         const std::string &upic);

        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
    };
}
