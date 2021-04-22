import requests
import re
from bs4 import BeautifulSoup as bs
from requests_html import HTMLSession

InstagramLink = input ("Digite o link da foto ou video: ")

url = InstagramLink

session = HTMLSession()

resp = session.get(url)

resp.html.render()

resp.html.html 

IGhtml= resp.html.html

#IGlink = IGhtml.split("media_overlay_info":null,"media_preview":null,"display_url":) [1].split('"')[0])

#print (IGlink)

#("display_url" :)
#(,"display_resources")
