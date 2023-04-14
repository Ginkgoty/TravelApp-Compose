#pragma once

#include <drogon/HttpController.h>

using namespace drogon;

namespace TravelApp {
    class Note : public drogon::HttpController<Note> {
    public:
        METHOD_LIST_BEGIN
            ADD_METHOD_TO(Note::getNoteList, "/note", Get);
            ADD_METHOD_TO(Note::getNoteDetail, "/note/detail?nid={}", Get);
            ADD_METHOD_TO(Note::uploadNote, "note/upload", Post, "TravelApp::LoginFilter");
            // use METHOD_ADD to add your custom processing function here;
            // METHOD_ADD(Note::get, "/{2}/{1}", Get); // path is /TravelApp/Note/{arg2}/{arg1}
            // METHOD_ADD(Note::your_method_name, "/{1}/{2}/list", Get); // path is /TravelApp/Note/{arg1}/{arg2}/list
            // ADD_METHOD_TO(Note::your_method_name, "/absolute/path/{1}/{2}/list", Get); // path is /absolute/path/{arg1}/{arg2}/list
        METHOD_LIST_END

        // your declaration of processing function maybe like this:
        void getNoteList(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        void getNoteDetail(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback, int nid);

        void uploadNote(const HttpRequestPtr &req, std::function<void(const HttpResponsePtr &)> &&callback);

        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
    };
}
