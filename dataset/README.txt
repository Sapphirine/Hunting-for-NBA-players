# To run our huntingforNBAplayer data grabbing to, first import relevant libraries as below. 

import sys
sys.path.append('Path-to-the_huntingforNBAplayers_directory’)
import huntingforNBAplayers as hp
import pandas
from bs4 import BeautifulSoup
reload(sys)   
sys.setdefaultencoding('utf8')

# Then use the following command to load an existing dataset. (The dataset has been updated 
# to the early December, if you want a latest one, use hp.buildPlayerDictionary method)

players = hp.loadPlayerDictionary('Path-to-the_huntingforNBAplayers_directory/players.json')

# Then you can use following command to get all players’ Per 100 Possession data and convert 
# them to a csv file. (Note that the first line will run about 10 minutes)

all_players = hp.getall(players)
all_players.to_csv('/home/bigdata/Downloads/huntingforNBAplayers/data.csv')