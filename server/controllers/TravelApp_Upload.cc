/**
 * @file TravelApp_Upload.cc
 * @author Li Jiawen (nmjbh@qq.com)
 * @brief 
 * @version 1.0
 * @date 2023-04-01
 * 
 * @copyright Copyright (c) 2023
 * 
 */
#include "TravelApp_Upload.h"

#include <Poco/String.h>

using namespace TravelApp;

void
Upload::asyncHandleHttpRequest(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback) {
    LOG_INFO << "File Upload Called!";
    Json::Value ret;
    MultiPartParser fileUpload;
    if (fileUpload.parse(req) != 0 || fileUpload.getFiles().size() != 1) {
        auto resp = HttpResponse::newHttpResponse();
        resp->setBody("Must only be one file");
        resp->setStatusCode(k403Forbidden);
        callback(resp);
        return;
    }

    auto params = fileUpload.getParameters();
    auto &file = fileUpload.getFiles()[0];

    if (Poco::replace(params["md5"], "\"", "") == Poco::toLower(file.getMd5())) {
        file.saveAs(Poco::replace(params["md5"], "\"", "") + "." + Poco::replace(params["type"], "\"", ""));
        ret["result"] = true;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        callback(resp);
    } else {
        ret["result"] = false;
        auto resp = HttpResponse::newHttpJsonResponse(ret);
        resp->setStatusCode(drogon::k500InternalServerError);
        callback(resp);
    }
}
