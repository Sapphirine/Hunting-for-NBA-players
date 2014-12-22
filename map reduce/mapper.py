#!/usr/bin/python
import sys

def mapper():
    count = 0
    # read standard input line by line
    for line in sys.stdin:
        # strip off extra whitespace, split on comma and put the data in an array
        data = line.strip().split(",")
        #print len(data)
        #print data[0]
        #print data[22]
        #print data[22]
        #print data[23]

        if len(data) == 24:

            count += 1
            name = data[0]
            season = data[1]
            team = data[2]
            position = data[3]
            game = data[4]
            gameS = data[5]
            MP = data[6]
            FG = data[7]
            FGA = data[8]
            FGper = data[9]
            FT = data[10]
            FTA = data[11]
            FTper = data[12]
            ORB = data[13]
            DRB = data[14]
            TRB = data[15]
            AST = data[16]
            STL = data[17]
            BLK = data[18]
            TOV = data[19]
            PF = data[20]
            PTS = data[21]
            OFF = data[22]
            DEF = data[23]
        
            # Now print out the data that will be passed to the reducer
            if count >= 1:
                print "{0}\t{1}\t{2}\t{3}\t{4}\t{5}\t{6}\t{7}\t{8}\t{9}\t{10}\t{11}\t{12}\t{13}\t{14}\t{15}\t{16}\t{17}\t{18}\t{19}\t{20}\t{21}\t{22}\t{23}".format(
                    name, season, team, position, game, gameS, MP, FG, FGA, FGper, FT, FTA, FTper, ORB, DRB, TRB, AST, STL, BLK, TOV, PF, PTS, OFF, DEF)
                    #data[0],data[1],data[2],data[3],data[4],data[5],data[6],data[7],data[8],data[9],data[10],data[11],data[12],data[13],data[14],
                    #data[15],data[16],data[17],data[18],data[19],data[20],data[21],data[22],data[23])
        #print len(data)
mapper()

        