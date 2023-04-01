/**
 *
 *  TravelApp_LoginFilter.cc
 *
 */

#include "TravelApp_LoginFilter.h"

using namespace drogon;
using namespace TravelApp;

void LoginFilter::doFilter(const HttpRequestPtr &req,
                           FilterCallback &&fcb,
                           FilterChainCallback &&fccb) {
    //Edit your logic here
    auto json = req->getJsonObject();
    if (json != nullptr && !(*json)["token"].asString().empty()) {
        LOG_DEBUG << "Token Found!";
        //Passed
        fccb();
        return;
    }
    //Check failed
    auto res = drogon::HttpResponse::newHttpResponse();
    res->setStatusCode(k500InternalServerError);
    fcb(res);
}
