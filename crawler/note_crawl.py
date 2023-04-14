import psycopg2
from selenium import webdriver
import time
import requests
import hashlib
import re
import execjs
import json
from requests import utils
from selenium.webdriver.common.by import By
from urllib3 import disable_warnings
from urllib3.exceptions import InsecureRequestWarning
from bs4 import BeautifulSoup
from bs4 import NavigableString
from psycopg2 import errors

disable_warnings(InsecureRequestWarning)

BASE_URL = "https://www.mafengwo.cn"

header = {
    # 'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng*/*;q=0.8,'
    #           'application/signed-exchange;v=b3;q=0.7',
    'User-agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/110.0.0.0 Safari/537.36',
}


#  游记的内容项——0，标题；1，文字；2，图片
class Item:
    def __init__(self, kind, content):
        self.kind = kind
        self.content = content


# 反反爬虫机制
def against_anti_crawl(url):
    # -- Requests 反爬
    # 第一次访问
    session = requests.session()
    response = session.get(url, headers=header, verify=False)  # 直接访问得到JS代码
    js_clearance = re.findall('cookie=(.*?);location', response.text)[0]  # 用正则表达式匹配出需要的部分
    result = execjs.eval(js_clearance).split(';')[0].split('=')[1]  # 反混淆、分割出cookie的部分

    # 第二次访问
    requests.utils.add_dict_to_cookiejar(session.cookies, {'__jsl_clearance_s': result})  # 将第一次访问的cookie添加进入session会话中
    response = session.get(url, headers=header, verify=False)  # 带上更新后的cookie进行第二次访问

    # 解密
    go = json.loads(re.findall(r'};go\((.*?)\)</script>', response.text)[0])
    for i in range(len(go['chars'])):
        for j in range(len(go['chars'])):
            values = go['bts'][0] + go['chars'][i] + go['chars'][j] + go['bts'][1]
            if go['ha'] == 'md5':
                ha = hashlib.md5(values.encode()).hexdigest()
            elif go['ha'] == 'sha1':
                ha = hashlib.sha1(values.encode()).hexdigest()
            elif go['ha'] == 'sha256':
                ha = hashlib.sha256(values.encode()).hexdigest()
            if ha == go['ct']:
                __jsl_clearance_s = values

    # 第三次访问
    requests.utils.add_dict_to_cookiejar(session.cookies, {'__jsl_clearance_s': __jsl_clearance_s})
    session.get(url=url, headers=header, verify=False)
    return session


# 获取游记列表
def get_note_list():
    note_list = []
    session = against_anti_crawl(BASE_URL)
    db = psycopg2.connect(host='118.31.67.238',
                          port='5433',
                          user='postgres',
                          password='47zyetnF&Urx',
                          database='travelapp')

    # 开启自动提交
    db.autocommit = True
    cursor = db.cursor()
    for page in range(1, 6):
        index = crawl_index_html(session=session, url=BASE_URL, page=page)
        # with open("index.html", encoding="utf-8", mode="w") as f:
        #     f.write(index)
        bs = BeautifulSoup(index, "html.parser")
        content_box = bs.find("div", id="_j_tn_content")
        notes = content_box.find_all("div", class_="tn-item clearfix")

        for note in notes:
            url = BASE_URL + note.find("a")['href']
            img_tag = note.find("img")
            if img_tag.has_attr("data-rt-src"):
                img = img_tag['data-rt-src']
            else:
                img = img_tag['src']
            title = note.find("dt").text.replace(" ", "").replace("\n", " ")
            intro = note.find("dd").text.replace(" ", "").replace("\n", " ")
            rname = note.find("a", class_="_j_gs_item").text
            nid = re.findall("\\d+", url)[0]
            print(nid, img, title, intro, rname)
            sql_string = "INSERT INTO note VALUES (%s,%s,%s,%s,%s)"
            try:
                cursor.execute(sql_string, (nid, img, title, intro, rname))
            except psycopg2.errors.UniqueViolation as e:
                print(e)
            note_list.append(url)
    cursor.close()
    db.close()
    return note_list


# 爬取主页中的游记
def crawl_index_html(session, url, page):
    # -- Selenium访问
    driver = webdriver.Edge()
    driver.implicitly_wait(1)
    driver.get(url)
    # print(driver.get_cookies())
    driver.delete_all_cookies()
    # print(driver.get_cookies())
    # 获取requests侧的cookies
    for k, v in session.cookies.items():
        cookie = {'name': k, 'value': v}
        driver.add_cookie(cookie_dict=cookie)
        # browser.add_cookie(cookie_dict={'name': k, 'value': v})
        # browser.add_cookie({'name': str(k), 'value': str(v)})
    # print(driver.get_cookies())
    driver.execute_cdp_cmd("Page.addScriptToEvaluateOnNewDocument", {
        "source": """
                Object.defineProperty(navigator, 'webdriver', {
                  get: () => undefined
                })
              """
    })
    driver.get(url)
    time.sleep(2)
    #  click page button
    if page > 1:
        driver.find_element(By.XPATH, f'//*[@id="_j_tn_pagination"]/a[{page - 1}]').click()
    time.sleep(2)
    js = "window.scrollTo(0, document.body.scrollHeight)"
    for j in range(5):
        driver.execute_script(js)
        time.sleep(0.5)

    article = driver.execute_script("return document.documentElement.outerHTML")
    # with open("temp.html", encoding="utf-8", mode="w") as f:
    #     f.write(article)
    return article


# 爬取游记详情
def crawl_note_html(session, url):
    # -- Selenium访问
    driver = webdriver.Edge()
    driver.implicitly_wait(1)
    driver.get(url)
    # print(driver.get_cookies())
    driver.delete_all_cookies()
    # print(driver.get_cookies())
    # 获取requests侧的cookies
    for k, v in session.cookies.items():
        cookie = {'name': k, 'value': v}
        driver.add_cookie(cookie_dict=cookie)
        # browser.add_cookie(cookie_dict={'name': k, 'value': v})
        # browser.add_cookie({'name': str(k), 'value': str(v)})
    # print(driver.get_cookies())
    driver.execute_cdp_cmd("Page.addScriptToEvaluateOnNewDocument", {
        "source": """
                Object.defineProperty(navigator, 'webdriver', {
                  get: () => undefined
                })
              """
    })
    driver.get(url)
    time.sleep(2)

    # 下拉滚动条
    js = "window.scrollTo(0, document.body.scrollHeight)"
    for j in range(5):
        driver.execute_script(js)
        time.sleep(0.5)

    article = driver.execute_script("return document.documentElement.outerHTML")

    # with open("temp.html", encoding="utf-8", mode="w") as f:
    #     f.write(article)

    return article


#  分析游记信息
def analyse_note(url):
    if not hasattr(analyse_note, 'uid'):
        analyse_note.uid = 0
    analyse_note.uid += 1
    # 获取游记的html
    session = against_anti_crawl(url=url)
    html = crawl_note_html(session=session, url=url)
    bs = BeautifulSoup(html, "html.parser")
    try:
        # nid
        nid = int(re.findall("\\d+", url)[0])
        # 头图
        background = bs.find("div", class_='set_bg _j_load_cover').find("img")['src']
        # 用户名
        uname = bs.find("a", class_="per_name")['title']
        # 用户图片
        upic = bs.find("div", class_='person').find("img")['src']
        # 时间
        t = bs.find("span", class_="time").text
        # 标题
        title = bs.find("div", class_="_j_titlebg").text.lstrip("\n").rstrip("\n")
        # 内容
        content_box = bs.find("div", class_="_j_content_box")
        items = content_box.find_all(attrs={'class': ["_j_note_content _j_seqitem", "add_pic _j_anchorcnt _j_seqitem",
                                                      "article_title _j_anchorcnt _j_seqitem"]})
        content = []
        for item in items:
            if item.name == 'p':
                a_tags = item.find_all("a")
                for a_tag in a_tags:
                    previous = a_tag.previous_sibling.text
                    string = NavigableString(previous.rstrip(" ").rstrip('\n'))
                    a_tag.previous_sibling.replace_with(string)

                    behind = a_tag.next_sibling.text
                    string = NavigableString(behind.lstrip(" ").lstrip('\n'))
                    a_tag.next_sibling.replace_with(string)

                    a_tag.insert_before(a_tag.text.rstrip(" ").rstrip('\n').lstrip(" ").lstrip('\n'))
                    a_tag.decompose()
                text = item.text.lstrip('\n').rstrip('\n').replace(" ", "")
                content.append(Item(kind=1, content=text).__dict__)
            if item.name == 'div':
                if 'article_title' in item.attrs['class']:
                    text = item.text.lstrip('\n').rstrip('\n').replace(" ", "").replace("\n", "")
                    content.append(Item(kind=0, content=text).__dict__)
                else:
                    img = item.find('img')
                    url = img['data-rt-src']
                    content.append(Item(kind=2, content=url).__dict__)
        content_json = json.dumps(content, ensure_ascii=False)
        return nid, background, analyse_note.uid, uname, upic, t, title, content_json
    except Exception as e:
        print(e)


def crawl():
    note_list = get_note_list()
    db = psycopg2.connect(host='118.31.67.238',
                          port='5433',
                          user='postgres',
                          password='47zyetnF&Urx',
                          database='travelapp')

    # 开启自动提交
    db.autocommit = True
    cursor = db.cursor()

    for note_url in note_list:
        try:
            result = analyse_note(note_url)
            sql_string = 'INSERT INTO traveler VALUES (\'{}\',\'{}\',\'{}\',\'{}\')'.format(
                result[2], result[3], result[4], '7c222fb2927d828af22f592134e8932480637c0d'
            )
            cursor.execute(sql_string)
            sql_string = 'INSERT INTO note_detail VALUES (\'{}\',\'{}\',\'{}\',\'{}\',\'{}\',\'{}\')'.format(
                result[0], result[1], result[2], result[5], result[6], result[7])
            print(sql_string)
            cursor.execute(sql_string)
        except Exception as e:
            print(e)
    cursor.close()
    db.close()


if __name__ == '__main__':
    crawl()
