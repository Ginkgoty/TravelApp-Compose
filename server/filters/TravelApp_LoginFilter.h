/**
 *
 *  TravelApp_LoginFilter.h
 *
 */

#pragma once

#include <drogon/HttpFilter.h>
using namespace drogon;
namespace TravelApp
{

class LoginFilter : public HttpFilter<LoginFilter>
{
  public:
    LoginFilter() = default;
    void doFilter(const HttpRequestPtr &req,
                  FilterCallback &&fcb,
                  FilterChainCallback &&fccb) override;
};

}
