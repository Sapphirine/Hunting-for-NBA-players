import time
import json
import string
import pandas
import logging
import requests

from bs4 import BeautifulSoup

BASKETBALL_LOG = 'basketball.log'

logging.basicConfig(filename=BASKETBALL_LOG,
                    level=logging.DEBUG,
                   )

__all__ = ['test', 'buildPlayerDictionary', 'searchForName', 'savePlayerDictionary', 'loadPlayerDictionary']

def getSoupFromURL(url, supressOutput=True):
    """
    This function grabs the url and returns and returns the BeautifulSoup object
    """
    if not supressOutput:
        print url
    
    try:
        r = requests.get(url)
    except:
        return None
    
    return BeautifulSoup(r.text)

def getCurrentPlayerNamesAndURLS(supressOutput=True):
    
    names = []
    
    for letter in string.ascii_lowercase:
        letter_page = getSoupFromURL('http://www.basketball-reference.com/players/%s/' % (letter), supressOutput)

        current_names = letter_page.findAll('strong')
        for n in current_names:
            name_data = n.children.next()
            names.append((name_data.contents[0], 'http://www.basketball-reference.com' + name_data.attrs['href']))

    return dict(names)

def buildPlayerDictionary(supressOutput=True):

    logging.debug("Begin grabbing name list")
    playerNamesAndURLS = getCurrentPlayerNamesAndURLS(supressOutput)
    logging.debug("Name list grabbing complete")

    players={}
    for name, url in playerNamesAndURLS.items():
        players[name] = {'overview_url':url}
        players[name]['overview_url_content'] = None
	players[name]['perposs'] = []
 
    logging.debug("Grabbing player overview URLs")

    for i, (name, player_dict) in enumerate(players.items()):
        if players[name]['overview_url_content'] is None:
            if not supressOutput:
                print i, 
        
            overview_soup = getSoupFromURL(players[name]['overview_url'], supressOutput)
            players[name]['overview_url_content'] = overview_soup.text

    logging.debug("buildPlayerDictionary complete")
    
    return players

def searchForName(playerDictionary, search_string):

    search_string = search_string.lower()
    return [name for name in playerDictionary.keys() if search_string in name.lower()]

def savePlayerDictionary(playerDictionary, pathToFile):

    json.dump(playerDictionary, open(pathToFile, 'wb'), indent=0)

def loadPlayerDictionary(pathToFile):

    f = open(pathToFile)
    json_string = f.read()
    return json.loads(json_string)

def dfFromGameLogURLList(gamelogs, name):

    return pandas.concat([dfFromGameLogURL(g, name) for g in gamelogs])

def dfFromGameLogURL(url, name):

    glsoup = getSoupFromURL(url)

    per_poss_table = glsoup.findAll('table', attrs={'id': 'per_poss'}) 

    header = []
    for th in per_poss_table[0].findAll('th'):
        if not th.getText() in header:
            header.append(th.getText())

    perposs = soupTableToDF(per_poss_table, header)

    perposs['Name'] = name
    perposs.set_index('Name')

    return perposs
    
def soupTableToDF(table_soup, header):

    if not table_soup:
        return None
    else:
        rows = table_soup[0].findAll('tr')[1:]
        rows = [r for r in rows if len(r.findAll('td')) > 0]
        parsed_table = [[col.getText().split(' ')[0] for col in row.findAll('td')] for row in rows]
        return pandas.io.parsers.TextParser(parsed_table, names=header, index_col=1, parse_dates=True).get_chunk()

def gameLogs(playerDictionary, name):

    return dfFromGameLogURLList(playerDictionary[name]['perposs'], name)

def getall(playerDictionary):

    tablesum=gameLogs(playerDictionary, 'Kevin Durant')

    for key in playerDictionary:
        print key
        table=gameLogs(playerDictionary, key)
        tablesum = tablesum.append(table)

    return tablesum
        

