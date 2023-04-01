/**
 * @file TravelApp_Upload.h
 * @author Li Jiawen (nmjbh@qq.com)
 * @brief 
 * @version 1.0
 * @date 2023-04-01
 * 
 * @copyright Copyright (c) 2023
 * 
 */
#pragma once

#include <drogon/HttpSimpleController.h>

using namespace drogon;

namespace TravelApp {
    class Upload : public drogon::HttpSimpleController<Upload> {
    public:
        void asyncHandleHttpRequest(const HttpRequestPtr &req,
                                    std::function<void(const HttpResponsePtr &)> &&callback) override;

        PATH_LIST_BEGIN
            // list path definitions here;
            // PATH_ADD("/path", "filter1", "filter2", HttpMethod1, HttpMethod2...);
            PATH_ADD("/upload");
        PATH_LIST_END
    };
}
