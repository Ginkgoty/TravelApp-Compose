#pragma once

#include <drogon/HttpController.h>
#include <PtComment.h>
#include <Traveler.h>

using namespace drogon;

namespace TravelApp {
    class Comment {
    public:
        int cid;
        int ptid;
        int uid;
        std::string uname;
        std::string upic;
        trantor::Date datetime;
        int belong;
        std::string comment;
        std::vector<std::shared_ptr<Comment>> replies;

//        Comment() = default;

        Comment(const drogon_model::travelapp::PtComment &ptComment,
                const drogon_model::travelapp::Traveler &traveler) {
            cid = ptComment.getValueOfCid();
            ptid = ptComment.getValueOfPtid();
            uid = ptComment.getValueOfUid();
            datetime = ptComment.getValueOfDatetime();
            belong = ptComment.getValueOfBelong();
            comment = ptComment.getValueOfComment();
            uname = traveler.getValueOfUname();
            upic = traveler.getValueOfUpic();
        }
    };

    Json::Value commentToJson(const std::shared_ptr<Comment> &comment) {
        Json::Value jsonComment;
        jsonComment["cid"] = comment->cid;
        jsonComment["ptid"] = comment->ptid;
        jsonComment["uid"] = comment->uid;
        jsonComment["uname"] = comment->uname;
        jsonComment["upic"] = comment->upic;
        jsonComment["datetime"] = comment->datetime.toCustomedFormattedStringLocal("%Y-%m-%d");
        jsonComment["belong"] = comment->belong;
        jsonComment["comment"] = comment->comment;

        Json::Value jsonReplies(Json::arrayValue);
        for (const auto &reply: comment->replies) {
            jsonReplies.append(commentToJson(reply));
        }
        jsonComment["replies"] = jsonReplies;

        return jsonComment;
    }

    std::vector<std::shared_ptr<Comment>> buildCommentsTree(const std::vector<std::shared_ptr<Comment>> &comments,
                                                            int parent = 0) {
        std::vector<std::shared_ptr<Comment>> tree;

        for (const auto &comment: comments) {
            if (comment->belong == parent) {
                auto newComment = std::make_shared<Comment>(*comment);
                newComment->replies = buildCommentsTree(comments, comment->cid);
                tree.push_back(newComment);
            }
        }

        return tree;
    }

    class PtComment : public drogon::HttpController<PtComment> {
    public:
        METHOD_LIST_BEGIN
            // use METHOD_ADD to add your custom processing function here;
            // METHOD_ADD(PtComment::get, "/{2}/{1}", Get); // path is /TravelApp/PtComment/{arg2}/{arg1}
            // METHOD_ADD(PtComment::your_method_name, "/{1}/{2}/list", Get); // path is /TravelApp/PtComment/{arg1}/{arg2}/list
            ADD_METHOD_TO(PtComment::comment, "/ptcomment", Post, "TravelApp::LoginFilter");
            ADD_METHOD_TO(PtComment::fetchComments, "/ptcomment?ptid={ptid}", Get);
        METHOD_LIST_END

        // your declaration of processing function maybe like this:
        // void get(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, int p1, std::string p2);
        // void your_method_name(const HttpRequestPtr& req, std::function<void (const HttpResponsePtr &)> &&callback, double p1, int p2) const;
        void comment(const drogon::HttpRequestPtr &req,
                     std::function<void(const drogon::HttpResponsePtr &)> &&callback);

        void fetchComments(const drogon::HttpRequestPtr &req,
                           std::function<void(const drogon::HttpResponsePtr &)> &&callback, int ptid);
    };
}
