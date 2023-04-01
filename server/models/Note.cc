/**
 *
 *  Note.cc
 *  DO NOT EDIT. This file is generated by drogon_ctl
 *
 */

#include "Note.h"
#include <drogon/utils/Utilities.h>
#include <string>

using namespace drogon;
using namespace drogon::orm;
using namespace drogon_model::travelapp;

const std::string Note::Cols::_nid = "nid";
const std::string Note::Cols::_img = "img";
const std::string Note::Cols::_title = "title";
const std::string Note::Cols::_intro = "intro";
const std::string Note::Cols::_rname = "rname";
const std::string Note::primaryKeyName = "nid";
const bool Note::hasPrimaryKey = true;
const std::string Note::tableName = "note";

const std::vector<typename Note::MetaData> Note::metaData_={
{"nid","int32_t","integer",4,0,1,1},
{"img","std::string","text",0,0,0,1},
{"title","std::string","character varying",255,0,0,1},
{"intro","std::string","text",0,0,0,1},
{"rname","std::string","character varying",255,0,0,1}
};
const std::string &Note::getColumnName(size_t index) noexcept(false)
{
    assert(index < metaData_.size());
    return metaData_[index].colName_;
}
Note::Note(const Row &r, const ssize_t indexOffset) noexcept
{
    if(indexOffset < 0)
    {
        if(!r["nid"].isNull())
        {
            nid_=std::make_shared<int32_t>(r["nid"].as<int32_t>());
        }
        if(!r["img"].isNull())
        {
            img_=std::make_shared<std::string>(r["img"].as<std::string>());
        }
        if(!r["title"].isNull())
        {
            title_=std::make_shared<std::string>(r["title"].as<std::string>());
        }
        if(!r["intro"].isNull())
        {
            intro_=std::make_shared<std::string>(r["intro"].as<std::string>());
        }
        if(!r["rname"].isNull())
        {
            rname_=std::make_shared<std::string>(r["rname"].as<std::string>());
        }
    }
    else
    {
        size_t offset = (size_t)indexOffset;
        if(offset + 5 > r.size())
        {
            LOG_FATAL << "Invalid SQL result for this model";
            return;
        }
        size_t index;
        index = offset + 0;
        if(!r[index].isNull())
        {
            nid_=std::make_shared<int32_t>(r[index].as<int32_t>());
        }
        index = offset + 1;
        if(!r[index].isNull())
        {
            img_=std::make_shared<std::string>(r[index].as<std::string>());
        }
        index = offset + 2;
        if(!r[index].isNull())
        {
            title_=std::make_shared<std::string>(r[index].as<std::string>());
        }
        index = offset + 3;
        if(!r[index].isNull())
        {
            intro_=std::make_shared<std::string>(r[index].as<std::string>());
        }
        index = offset + 4;
        if(!r[index].isNull())
        {
            rname_=std::make_shared<std::string>(r[index].as<std::string>());
        }
    }

}

Note::Note(const Json::Value &pJson, const std::vector<std::string> &pMasqueradingVector) noexcept(false)
{
    if(pMasqueradingVector.size() != 5)
    {
        LOG_ERROR << "Bad masquerading vector";
        return;
    }
    if(!pMasqueradingVector[0].empty() && pJson.isMember(pMasqueradingVector[0]))
    {
        dirtyFlag_[0] = true;
        if(!pJson[pMasqueradingVector[0]].isNull())
        {
            nid_=std::make_shared<int32_t>((int32_t)pJson[pMasqueradingVector[0]].asInt64());
        }
    }
    if(!pMasqueradingVector[1].empty() && pJson.isMember(pMasqueradingVector[1]))
    {
        dirtyFlag_[1] = true;
        if(!pJson[pMasqueradingVector[1]].isNull())
        {
            img_=std::make_shared<std::string>(pJson[pMasqueradingVector[1]].asString());
        }
    }
    if(!pMasqueradingVector[2].empty() && pJson.isMember(pMasqueradingVector[2]))
    {
        dirtyFlag_[2] = true;
        if(!pJson[pMasqueradingVector[2]].isNull())
        {
            title_=std::make_shared<std::string>(pJson[pMasqueradingVector[2]].asString());
        }
    }
    if(!pMasqueradingVector[3].empty() && pJson.isMember(pMasqueradingVector[3]))
    {
        dirtyFlag_[3] = true;
        if(!pJson[pMasqueradingVector[3]].isNull())
        {
            intro_=std::make_shared<std::string>(pJson[pMasqueradingVector[3]].asString());
        }
    }
    if(!pMasqueradingVector[4].empty() && pJson.isMember(pMasqueradingVector[4]))
    {
        dirtyFlag_[4] = true;
        if(!pJson[pMasqueradingVector[4]].isNull())
        {
            rname_=std::make_shared<std::string>(pJson[pMasqueradingVector[4]].asString());
        }
    }
}

Note::Note(const Json::Value &pJson) noexcept(false)
{
    if(pJson.isMember("nid"))
    {
        dirtyFlag_[0]=true;
        if(!pJson["nid"].isNull())
        {
            nid_=std::make_shared<int32_t>((int32_t)pJson["nid"].asInt64());
        }
    }
    if(pJson.isMember("img"))
    {
        dirtyFlag_[1]=true;
        if(!pJson["img"].isNull())
        {
            img_=std::make_shared<std::string>(pJson["img"].asString());
        }
    }
    if(pJson.isMember("title"))
    {
        dirtyFlag_[2]=true;
        if(!pJson["title"].isNull())
        {
            title_=std::make_shared<std::string>(pJson["title"].asString());
        }
    }
    if(pJson.isMember("intro"))
    {
        dirtyFlag_[3]=true;
        if(!pJson["intro"].isNull())
        {
            intro_=std::make_shared<std::string>(pJson["intro"].asString());
        }
    }
    if(pJson.isMember("rname"))
    {
        dirtyFlag_[4]=true;
        if(!pJson["rname"].isNull())
        {
            rname_=std::make_shared<std::string>(pJson["rname"].asString());
        }
    }
}

void Note::updateByMasqueradedJson(const Json::Value &pJson,
                                            const std::vector<std::string> &pMasqueradingVector) noexcept(false)
{
    if(pMasqueradingVector.size() != 5)
    {
        LOG_ERROR << "Bad masquerading vector";
        return;
    }
    if(!pMasqueradingVector[0].empty() && pJson.isMember(pMasqueradingVector[0]))
    {
        if(!pJson[pMasqueradingVector[0]].isNull())
        {
            nid_=std::make_shared<int32_t>((int32_t)pJson[pMasqueradingVector[0]].asInt64());
        }
    }
    if(!pMasqueradingVector[1].empty() && pJson.isMember(pMasqueradingVector[1]))
    {
        dirtyFlag_[1] = true;
        if(!pJson[pMasqueradingVector[1]].isNull())
        {
            img_=std::make_shared<std::string>(pJson[pMasqueradingVector[1]].asString());
        }
    }
    if(!pMasqueradingVector[2].empty() && pJson.isMember(pMasqueradingVector[2]))
    {
        dirtyFlag_[2] = true;
        if(!pJson[pMasqueradingVector[2]].isNull())
        {
            title_=std::make_shared<std::string>(pJson[pMasqueradingVector[2]].asString());
        }
    }
    if(!pMasqueradingVector[3].empty() && pJson.isMember(pMasqueradingVector[3]))
    {
        dirtyFlag_[3] = true;
        if(!pJson[pMasqueradingVector[3]].isNull())
        {
            intro_=std::make_shared<std::string>(pJson[pMasqueradingVector[3]].asString());
        }
    }
    if(!pMasqueradingVector[4].empty() && pJson.isMember(pMasqueradingVector[4]))
    {
        dirtyFlag_[4] = true;
        if(!pJson[pMasqueradingVector[4]].isNull())
        {
            rname_=std::make_shared<std::string>(pJson[pMasqueradingVector[4]].asString());
        }
    }
}

void Note::updateByJson(const Json::Value &pJson) noexcept(false)
{
    if(pJson.isMember("nid"))
    {
        if(!pJson["nid"].isNull())
        {
            nid_=std::make_shared<int32_t>((int32_t)pJson["nid"].asInt64());
        }
    }
    if(pJson.isMember("img"))
    {
        dirtyFlag_[1] = true;
        if(!pJson["img"].isNull())
        {
            img_=std::make_shared<std::string>(pJson["img"].asString());
        }
    }
    if(pJson.isMember("title"))
    {
        dirtyFlag_[2] = true;
        if(!pJson["title"].isNull())
        {
            title_=std::make_shared<std::string>(pJson["title"].asString());
        }
    }
    if(pJson.isMember("intro"))
    {
        dirtyFlag_[3] = true;
        if(!pJson["intro"].isNull())
        {
            intro_=std::make_shared<std::string>(pJson["intro"].asString());
        }
    }
    if(pJson.isMember("rname"))
    {
        dirtyFlag_[4] = true;
        if(!pJson["rname"].isNull())
        {
            rname_=std::make_shared<std::string>(pJson["rname"].asString());
        }
    }
}

const int32_t &Note::getValueOfNid() const noexcept
{
    const static int32_t defaultValue = int32_t();
    if(nid_)
        return *nid_;
    return defaultValue;
}
const std::shared_ptr<int32_t> &Note::getNid() const noexcept
{
    return nid_;
}
void Note::setNid(const int32_t &pNid) noexcept
{
    nid_ = std::make_shared<int32_t>(pNid);
    dirtyFlag_[0] = true;
}
const typename Note::PrimaryKeyType & Note::getPrimaryKey() const
{
    assert(nid_);
    return *nid_;
}

const std::string &Note::getValueOfImg() const noexcept
{
    const static std::string defaultValue = std::string();
    if(img_)
        return *img_;
    return defaultValue;
}
const std::shared_ptr<std::string> &Note::getImg() const noexcept
{
    return img_;
}
void Note::setImg(const std::string &pImg) noexcept
{
    img_ = std::make_shared<std::string>(pImg);
    dirtyFlag_[1] = true;
}
void Note::setImg(std::string &&pImg) noexcept
{
    img_ = std::make_shared<std::string>(std::move(pImg));
    dirtyFlag_[1] = true;
}

const std::string &Note::getValueOfTitle() const noexcept
{
    const static std::string defaultValue = std::string();
    if(title_)
        return *title_;
    return defaultValue;
}
const std::shared_ptr<std::string> &Note::getTitle() const noexcept
{
    return title_;
}
void Note::setTitle(const std::string &pTitle) noexcept
{
    title_ = std::make_shared<std::string>(pTitle);
    dirtyFlag_[2] = true;
}
void Note::setTitle(std::string &&pTitle) noexcept
{
    title_ = std::make_shared<std::string>(std::move(pTitle));
    dirtyFlag_[2] = true;
}

const std::string &Note::getValueOfIntro() const noexcept
{
    const static std::string defaultValue = std::string();
    if(intro_)
        return *intro_;
    return defaultValue;
}
const std::shared_ptr<std::string> &Note::getIntro() const noexcept
{
    return intro_;
}
void Note::setIntro(const std::string &pIntro) noexcept
{
    intro_ = std::make_shared<std::string>(pIntro);
    dirtyFlag_[3] = true;
}
void Note::setIntro(std::string &&pIntro) noexcept
{
    intro_ = std::make_shared<std::string>(std::move(pIntro));
    dirtyFlag_[3] = true;
}

const std::string &Note::getValueOfRname() const noexcept
{
    const static std::string defaultValue = std::string();
    if(rname_)
        return *rname_;
    return defaultValue;
}
const std::shared_ptr<std::string> &Note::getRname() const noexcept
{
    return rname_;
}
void Note::setRname(const std::string &pRname) noexcept
{
    rname_ = std::make_shared<std::string>(pRname);
    dirtyFlag_[4] = true;
}
void Note::setRname(std::string &&pRname) noexcept
{
    rname_ = std::make_shared<std::string>(std::move(pRname));
    dirtyFlag_[4] = true;
}

void Note::updateId(const uint64_t id)
{
}

const std::vector<std::string> &Note::insertColumns() noexcept
{
    static const std::vector<std::string> inCols={
        "nid",
        "img",
        "title",
        "intro",
        "rname"
    };
    return inCols;
}

void Note::outputArgs(drogon::orm::internal::SqlBinder &binder) const
{
    if(dirtyFlag_[0])
    {
        if(getNid())
        {
            binder << getValueOfNid();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[1])
    {
        if(getImg())
        {
            binder << getValueOfImg();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[2])
    {
        if(getTitle())
        {
            binder << getValueOfTitle();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[3])
    {
        if(getIntro())
        {
            binder << getValueOfIntro();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[4])
    {
        if(getRname())
        {
            binder << getValueOfRname();
        }
        else
        {
            binder << nullptr;
        }
    }
}

const std::vector<std::string> Note::updateColumns() const
{
    std::vector<std::string> ret;
    if(dirtyFlag_[0])
    {
        ret.push_back(getColumnName(0));
    }
    if(dirtyFlag_[1])
    {
        ret.push_back(getColumnName(1));
    }
    if(dirtyFlag_[2])
    {
        ret.push_back(getColumnName(2));
    }
    if(dirtyFlag_[3])
    {
        ret.push_back(getColumnName(3));
    }
    if(dirtyFlag_[4])
    {
        ret.push_back(getColumnName(4));
    }
    return ret;
}

void Note::updateArgs(drogon::orm::internal::SqlBinder &binder) const
{
    if(dirtyFlag_[0])
    {
        if(getNid())
        {
            binder << getValueOfNid();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[1])
    {
        if(getImg())
        {
            binder << getValueOfImg();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[2])
    {
        if(getTitle())
        {
            binder << getValueOfTitle();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[3])
    {
        if(getIntro())
        {
            binder << getValueOfIntro();
        }
        else
        {
            binder << nullptr;
        }
    }
    if(dirtyFlag_[4])
    {
        if(getRname())
        {
            binder << getValueOfRname();
        }
        else
        {
            binder << nullptr;
        }
    }
}
Json::Value Note::toJson() const
{
    Json::Value ret;
    if(getNid())
    {
        ret["nid"]=getValueOfNid();
    }
    else
    {
        ret["nid"]=Json::Value();
    }
    if(getImg())
    {
        ret["img"]=getValueOfImg();
    }
    else
    {
        ret["img"]=Json::Value();
    }
    if(getTitle())
    {
        ret["title"]=getValueOfTitle();
    }
    else
    {
        ret["title"]=Json::Value();
    }
    if(getIntro())
    {
        ret["intro"]=getValueOfIntro();
    }
    else
    {
        ret["intro"]=Json::Value();
    }
    if(getRname())
    {
        ret["rname"]=getValueOfRname();
    }
    else
    {
        ret["rname"]=Json::Value();
    }
    return ret;
}

Json::Value Note::toMasqueradedJson(
    const std::vector<std::string> &pMasqueradingVector) const
{
    Json::Value ret;
    if(pMasqueradingVector.size() == 5)
    {
        if(!pMasqueradingVector[0].empty())
        {
            if(getNid())
            {
                ret[pMasqueradingVector[0]]=getValueOfNid();
            }
            else
            {
                ret[pMasqueradingVector[0]]=Json::Value();
            }
        }
        if(!pMasqueradingVector[1].empty())
        {
            if(getImg())
            {
                ret[pMasqueradingVector[1]]=getValueOfImg();
            }
            else
            {
                ret[pMasqueradingVector[1]]=Json::Value();
            }
        }
        if(!pMasqueradingVector[2].empty())
        {
            if(getTitle())
            {
                ret[pMasqueradingVector[2]]=getValueOfTitle();
            }
            else
            {
                ret[pMasqueradingVector[2]]=Json::Value();
            }
        }
        if(!pMasqueradingVector[3].empty())
        {
            if(getIntro())
            {
                ret[pMasqueradingVector[3]]=getValueOfIntro();
            }
            else
            {
                ret[pMasqueradingVector[3]]=Json::Value();
            }
        }
        if(!pMasqueradingVector[4].empty())
        {
            if(getRname())
            {
                ret[pMasqueradingVector[4]]=getValueOfRname();
            }
            else
            {
                ret[pMasqueradingVector[4]]=Json::Value();
            }
        }
        return ret;
    }
    LOG_ERROR << "Masquerade failed";
    if(getNid())
    {
        ret["nid"]=getValueOfNid();
    }
    else
    {
        ret["nid"]=Json::Value();
    }
    if(getImg())
    {
        ret["img"]=getValueOfImg();
    }
    else
    {
        ret["img"]=Json::Value();
    }
    if(getTitle())
    {
        ret["title"]=getValueOfTitle();
    }
    else
    {
        ret["title"]=Json::Value();
    }
    if(getIntro())
    {
        ret["intro"]=getValueOfIntro();
    }
    else
    {
        ret["intro"]=Json::Value();
    }
    if(getRname())
    {
        ret["rname"]=getValueOfRname();
    }
    else
    {
        ret["rname"]=Json::Value();
    }
    return ret;
}

bool Note::validateJsonForCreation(const Json::Value &pJson, std::string &err)
{
    if(pJson.isMember("nid"))
    {
        if(!validJsonOfField(0, "nid", pJson["nid"], err, true))
            return false;
    }
    else
    {
        err="The nid column cannot be null";
        return false;
    }
    if(pJson.isMember("img"))
    {
        if(!validJsonOfField(1, "img", pJson["img"], err, true))
            return false;
    }
    else
    {
        err="The img column cannot be null";
        return false;
    }
    if(pJson.isMember("title"))
    {
        if(!validJsonOfField(2, "title", pJson["title"], err, true))
            return false;
    }
    else
    {
        err="The title column cannot be null";
        return false;
    }
    if(pJson.isMember("intro"))
    {
        if(!validJsonOfField(3, "intro", pJson["intro"], err, true))
            return false;
    }
    else
    {
        err="The intro column cannot be null";
        return false;
    }
    if(pJson.isMember("rname"))
    {
        if(!validJsonOfField(4, "rname", pJson["rname"], err, true))
            return false;
    }
    else
    {
        err="The rname column cannot be null";
        return false;
    }
    return true;
}
bool Note::validateMasqueradedJsonForCreation(const Json::Value &pJson,
                                              const std::vector<std::string> &pMasqueradingVector,
                                              std::string &err)
{
    if(pMasqueradingVector.size() != 5)
    {
        err = "Bad masquerading vector";
        return false;
    }
    try {
      if(!pMasqueradingVector[0].empty())
      {
          if(pJson.isMember(pMasqueradingVector[0]))
          {
              if(!validJsonOfField(0, pMasqueradingVector[0], pJson[pMasqueradingVector[0]], err, true))
                  return false;
          }
        else
        {
            err="The " + pMasqueradingVector[0] + " column cannot be null";
            return false;
        }
      }
      if(!pMasqueradingVector[1].empty())
      {
          if(pJson.isMember(pMasqueradingVector[1]))
          {
              if(!validJsonOfField(1, pMasqueradingVector[1], pJson[pMasqueradingVector[1]], err, true))
                  return false;
          }
        else
        {
            err="The " + pMasqueradingVector[1] + " column cannot be null";
            return false;
        }
      }
      if(!pMasqueradingVector[2].empty())
      {
          if(pJson.isMember(pMasqueradingVector[2]))
          {
              if(!validJsonOfField(2, pMasqueradingVector[2], pJson[pMasqueradingVector[2]], err, true))
                  return false;
          }
        else
        {
            err="The " + pMasqueradingVector[2] + " column cannot be null";
            return false;
        }
      }
      if(!pMasqueradingVector[3].empty())
      {
          if(pJson.isMember(pMasqueradingVector[3]))
          {
              if(!validJsonOfField(3, pMasqueradingVector[3], pJson[pMasqueradingVector[3]], err, true))
                  return false;
          }
        else
        {
            err="The " + pMasqueradingVector[3] + " column cannot be null";
            return false;
        }
      }
      if(!pMasqueradingVector[4].empty())
      {
          if(pJson.isMember(pMasqueradingVector[4]))
          {
              if(!validJsonOfField(4, pMasqueradingVector[4], pJson[pMasqueradingVector[4]], err, true))
                  return false;
          }
        else
        {
            err="The " + pMasqueradingVector[4] + " column cannot be null";
            return false;
        }
      }
    }
    catch(const Json::LogicError &e)
    {
      err = e.what();
      return false;
    }
    return true;
}
bool Note::validateJsonForUpdate(const Json::Value &pJson, std::string &err)
{
    if(pJson.isMember("nid"))
    {
        if(!validJsonOfField(0, "nid", pJson["nid"], err, false))
            return false;
    }
    else
    {
        err = "The value of primary key must be set in the json object for update";
        return false;
    }
    if(pJson.isMember("img"))
    {
        if(!validJsonOfField(1, "img", pJson["img"], err, false))
            return false;
    }
    if(pJson.isMember("title"))
    {
        if(!validJsonOfField(2, "title", pJson["title"], err, false))
            return false;
    }
    if(pJson.isMember("intro"))
    {
        if(!validJsonOfField(3, "intro", pJson["intro"], err, false))
            return false;
    }
    if(pJson.isMember("rname"))
    {
        if(!validJsonOfField(4, "rname", pJson["rname"], err, false))
            return false;
    }
    return true;
}
bool Note::validateMasqueradedJsonForUpdate(const Json::Value &pJson,
                                            const std::vector<std::string> &pMasqueradingVector,
                                            std::string &err)
{
    if(pMasqueradingVector.size() != 5)
    {
        err = "Bad masquerading vector";
        return false;
    }
    try {
      if(!pMasqueradingVector[0].empty() && pJson.isMember(pMasqueradingVector[0]))
      {
          if(!validJsonOfField(0, pMasqueradingVector[0], pJson[pMasqueradingVector[0]], err, false))
              return false;
      }
    else
    {
        err = "The value of primary key must be set in the json object for update";
        return false;
    }
      if(!pMasqueradingVector[1].empty() && pJson.isMember(pMasqueradingVector[1]))
      {
          if(!validJsonOfField(1, pMasqueradingVector[1], pJson[pMasqueradingVector[1]], err, false))
              return false;
      }
      if(!pMasqueradingVector[2].empty() && pJson.isMember(pMasqueradingVector[2]))
      {
          if(!validJsonOfField(2, pMasqueradingVector[2], pJson[pMasqueradingVector[2]], err, false))
              return false;
      }
      if(!pMasqueradingVector[3].empty() && pJson.isMember(pMasqueradingVector[3]))
      {
          if(!validJsonOfField(3, pMasqueradingVector[3], pJson[pMasqueradingVector[3]], err, false))
              return false;
      }
      if(!pMasqueradingVector[4].empty() && pJson.isMember(pMasqueradingVector[4]))
      {
          if(!validJsonOfField(4, pMasqueradingVector[4], pJson[pMasqueradingVector[4]], err, false))
              return false;
      }
    }
    catch(const Json::LogicError &e)
    {
      err = e.what();
      return false;
    }
    return true;
}
bool Note::validJsonOfField(size_t index,
                            const std::string &fieldName,
                            const Json::Value &pJson,
                            std::string &err,
                            bool isForCreation)
{
    switch(index)
    {
        case 0:
            if(pJson.isNull())
            {
                err="The " + fieldName + " column cannot be null";
                return false;
            }
            if(!pJson.isInt())
            {
                err="Type error in the "+fieldName+" field";
                return false;
            }
            break;
        case 1:
            if(pJson.isNull())
            {
                err="The " + fieldName + " column cannot be null";
                return false;
            }
            if(!pJson.isString())
            {
                err="Type error in the "+fieldName+" field";
                return false;
            }
            break;
        case 2:
            if(pJson.isNull())
            {
                err="The " + fieldName + " column cannot be null";
                return false;
            }
            if(!pJson.isString())
            {
                err="Type error in the "+fieldName+" field";
                return false;
            }
            // asString().length() creates a string object, is there any better way to validate the length?
            if(pJson.isString() && pJson.asString().length() > 255)
            {
                err="String length exceeds limit for the " +
                    fieldName +
                    " field (the maximum value is 255)";
                return false;
            }

            break;
        case 3:
            if(pJson.isNull())
            {
                err="The " + fieldName + " column cannot be null";
                return false;
            }
            if(!pJson.isString())
            {
                err="Type error in the "+fieldName+" field";
                return false;
            }
            break;
        case 4:
            if(pJson.isNull())
            {
                err="The " + fieldName + " column cannot be null";
                return false;
            }
            if(!pJson.isString())
            {
                err="Type error in the "+fieldName+" field";
                return false;
            }
            // asString().length() creates a string object, is there any better way to validate the length?
            if(pJson.isString() && pJson.asString().length() > 255)
            {
                err="String length exceeds limit for the " +
                    fieldName +
                    " field (the maximum value is 255)";
                return false;
            }

            break;
        default:
            err="Internal error in the server";
            return false;
    }
    return true;
}