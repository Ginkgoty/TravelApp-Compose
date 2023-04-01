import requests
from bs4 import BeautifulSoup
import hashlib
import re
import execjs
import json
from requests import utils
from selenium import webdriver
from urllib3 import disable_warnings
from urllib3.exceptions import InsecureRequestWarning
import psycopg2
import time

disable_warnings(InsecureRequestWarning)

header = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) '
                  'Chrome/110.0.0.0 Safari/537.36',
}

spt_base_url = 'https://www.mafengwo.cn/poi/{}.html'

region_base_url = 'https://www.mafengwo.cn/jd/{}/gonglve.html'

regions = [
    '北京',
    '上海',
    '成都', '西安', '重庆',
    '杭州', '苏州', '南京', '丽江',
    '大理',
    '桂林', '厦门', '三亚', '拉萨', '青岛',
    '张家界', '呼伦贝尔', '武汉', '乌鲁木齐', '洛阳',
    '开封', '长沙', '南昌', '贵阳', '昆明',
    '太原', '兰州', '敦煌', '西宁', '广州',
    '深圳', '哈尔滨', '长春', '沈阳',
    '香港', '澳门'
]


# 根据关键词搜索地区代码
def search_region(keyword):
    search_base_url = 'https://www.mafengwo.cn/search/q.php?q='
    response = requests.get(url=search_base_url + keyword, headers=header)
    bs = BeautifulSoup(response.text, "html.parser")
    result = bs.find("div", class_="search-mdd-wrap")
    info = [result.find(class_='title').text, re.findall("id=\\d+&", result.find("a")['href'])[0][3:-1],
            re.findall('h.+\\?', result['style'])[0][:-1]]
    return info


# 获取地区html
def get_html(url):
    response = requests.get(url=url, headers=header)
    # with open(file="beijing.html", encoding="utf-8", mode="w") as f:
    #     f.write(response.text)
    return response.text


# 获取景点html，可能有反爬虫机制，但也可能无。
def get_html_with_against(url):
    response = get_session_with_against(url).get(url, headers=header, verify=False)
    return response.text


#  有反爬虫机制的获取方式。
def get_session_with_against(url):
    session = requests.session()  # 使用session会一直携带上一次的cookies

    # 第一次访问
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
    session.get(url, headers=header, verify=False)
    return session


# 景点过滤器
def spot_filter(tag):
    return tag.has_attr('href') and tag.has_attr('target') and tag.has_attr('title')


# 获取地区内的景点
def get_spots_in_region(url):
    bs = BeautifulSoup(get_html(url), "html.parser")
    introduction = bs.find(id="mdd_poi_desc").text
    spots = bs.findAll(name="a", attrs={'href': True, 'target': True, 'title': True,
                                        'class': False, 'data-cs-p': False})
    result = []
    for spot in spots:
        if spot.find("div") is None and spot.find("a") is None:
            spot_intro = spot.find_next("p").text
            image = spot.find_next("img")['src'].split('?')[0]
            result.append([spot['title'], spot_intro, image, re.findall(r"\d+", spot['href'])[0]])
        elif spot.find("div") is not None and spot.find("h3") is not None:
            spot_intro = spot.find("p").text
            image = spot.find("img")['src'].split('?')[0]
            result.append([spot['title'], spot_intro, image, re.findall(r"\d+", spot['href'])[0]])
    return introduction, result


# 获取景区详情
def get_spot_detail(spot_id):
    spot_url = spt_base_url.format(spot_id)
    bs = BeautifulSoup(get_html_with_against(spot_url), "html.parser")
    summary = bs.find('div', class_='summary').text.replace(' ', '')
    try:
        tel = bs.find('li', class_='tel').text
    except AttributeError:
        tel = '电话：无'
    time_spent = bs.find('li', class_='item-time').text
    result = []

    photo_url = 'https://www.mafengwo.cn/photo/poi/{}.html'.format(spot_id)
    session = get_session_with_against(photo_url)
    # -- Selenium访问，获取完整html
    driver = webdriver.Edge()
    driver.implicitly_wait(1)
    driver.get(photo_url)
    driver.delete_all_cookies()
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
    driver.get(photo_url)
    time.sleep(4)
    # js = "window.scrollTo(0, document.body.scrollHeight)"
    # for j in range(5):
    #     driver.execute_script(js)
    #     time.sleep(0.5)
    article = driver.execute_script("return document.documentElement.outerHTML")  # 获取完整html
    photo_bs = BeautifulSoup(article, "html.parser")
    album = photo_bs.find('div', class_="album-total").find_all("img")
    for i in range(3):
        result.append(album[i]['src'].split('?')[0])  # 图片
    result += [summary.lstrip("\n").rstrip("\n"), tel.lstrip("\n").rstrip("\n").replace('\n', "："),
               time_spent.lstrip("\n").rstrip("\n").replace('\n', "：")]  # 简介，电话，用时参考
    dls = bs.find("div", class_='mod mod-detail').find_all("dl")
    if len(dls) == 2:
        traffic = "无"
        ticket = dls[0].find('dd').text.lstrip('\n').lstrip().rstrip('\n')
        opening = dls[1].find('dd').text.lstrip('\n').lstrip().rstrip('\n')
    else:
        traffic = dls[0].find('dd').text.lstrip('\n').lstrip().rstrip('\n')
        ticket = dls[1].find('dd').text.lstrip('\n').lstrip().rstrip('\n')
        opening = dls[2].find('dd').text.lstrip('\n').lstrip().rstrip('\n')
    result.append(traffic)
    result.append(ticket)
    result.append(opening)
    location = bs.find("div", class_="mod mod-location mfw-acc-hide", ).find_next("p").text
    result.append(location)
    # 获取地理坐标信息
    header['Referer'] = spot_url
    loc_data = requests.get(
        url='https://pagelet.mafengwo.cn/poi/pagelet/poiLocationApi?params={"poi_id":"' + str(spot_id) + '"}',
        headers=header, verify=False)
    print(json.loads(loc_data.text)['data']['controller_data']['poi'])
    lat = json.loads(loc_data.text)['data']['controller_data']['poi']['lat']  # 纬度
    lng = json.loads(loc_data.text)['data']['controller_data']['poi']['lng']  # 经度
    result.append(lat)
    result.append(lng)
    return result


# 爬虫主函数
def crawl(keyword):
    db = psycopg2.connect(host='127.0.0.1',
                          port='5432',
                          user='postgres',
                          password='your password',
                          database='travelapp')

    # 开启自动提交
    db.autocommit = True
    cursor = db.cursor()
    try:
        # 获得地区名称，代码，图片
        region_info = search_region(keyword)
        region_url = region_base_url.format(region_info[1])

        # 获取地区内的景点
        info = get_spots_in_region(region_url)
        introduction = info[0]
        sql_string = 'INSERT INTO region VALUES (\'{}\',\'{}\',\'{}\',\'{}\')'.format(region_info[1], region_info[0],
                                                                                      introduction, region_info[2])
        print(sql_string + '\n')
        try:
            cursor.execute(sql_string)  # 写入地区信息
        except Exception as e:
            print(e)

        # 获取景点详情
        for spot in info[1]:
            sql_string = 'INSERT INTO spot VALUES (\'{}\',\'{}\',\'{}\',\'{}\',\'{}\',\'{}\')'.format(spot[3],
                                                                                                      region_info[1],
                                                                                                      spot[0],
                                                                                                      region_info[0],
                                                                                                      spot[2],
                                                                                                      spot[1])
            print(sql_string + '\n')
            try:
                cursor.execute(sql_string)  # 写入景点信息
            except Exception as e:
                print(e)
            detail = get_spot_detail(spot_id=spot[3])
            sql_string = "INSERT INTO detail VALUES (\'{}\',\'{}\',\'{}\',\'{}\',\'{}\',\'{}\',\'{}\',\'{}\',\'{}\'," \
                         "\'{}\',\'{}\',\'{}\',\'{}\')" \
                .format(spot[3], detail[3], detail[4], detail[5],
                        detail[6], detail[7], detail[8], detail[0],
                        detail[1], detail[2], detail[9], detail[10], detail[11])
            print(sql_string + '\n')
            try:
                cursor.execute(sql_string)  # 写入景点详情
            except Exception as e:
                print(e)
            # time.sleep(1)  # 防止反爬
        cursor.close()
        db.close()
    except Exception as e:
        print(e)


if __name__ == '__main__':
    for region in regions:
        crawl(region)
