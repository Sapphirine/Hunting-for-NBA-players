#!/usr/bin/python
import sys

def reducer():
    season = 0
    MPTotal = 0
    GameTotal = 0
    TOVTotal = 0 
    offRTotal = 0
    defRTotal = 0
    thisName = None
    oldName = None
    count = 0
    flag = False
    # read standard input line by line
    for line in sys.stdin:
        data = line.strip().split("\t")
        print len(data)
        print "here1"
        #if len(data) == 24:
            #continue
        #count += 1
        if  len(data) == 24:
            print "here2"
            thisName, thisSeason, thisTeam, thisPos, thisGame, thisGameS, thisMP, thisFG, thisFGA, thisFGper, thisFT, thisFTA, thisFTper, thisORB, thisDRB, thisTRB, thisAST, thisSTL, thisBLK, thisTOV, thisPF, thisPoint, thisOffR, thisDefR = data
            flag = True;

        if oldName and oldName != thisName:
            #print "here2"
            offR = thisOffR
            defR = thisDefR
            print "{0}\t{1}\t{2}".format(thisName, offR, defR)

            season = 0
            offRTotal = 0
            defRTotal = 0

        oldName = thisName
            
        if flag:
            oldName	= thisName
            season += 1
            try:
                thisPoint = float(thisPoint)
                thisTRB = float(thisTRB)
                thisDRB = float(thisDRB)
                thisORB = float(thisORB)
                thisTOV = float(thisTOV)
                thisAST = float(thisAST)
                thisBLK = float(thisBLK)
                thisMP = float(thisMP)
                thisFG = float(thisFG)
                thisFGA = float(thisFGA)
                thisFT = float(thisFT)
                thisFTA = float(thisFTA)

                #GameTotal += int(thisGame)
                #MPTotal += int(thisMP)
                #TOVTotal += int(thisTOV)
                #offRTotal += int(thisOffR)
                #defRTotal += int(thisDefR)
                if thisFTA != 0 and thisFGA != 0 and thisFG != 0 and thisFT != 0:
                    AS = ((thisMP/48) * (1.14 * thisAST)/thisFG + thisAST/48 * thisMP * 5 - thisAST)
                    FG_P = thisFG * (1 - 0.5 * (thisPoint - thisFT)/(2 * thisFGA)) * AS
                    AS_P = 0.5 * ((thisPoint - thisFT)/(2 * thisFGA - thisFG))
                    FT_P = 1 - 0.5 * ((thisFT/thisFTA) * (thisFT/thisFTA) * 0.4 * thisFTA)
                    SP = thisFG + (1 - (1 - (thisFT/thisFTA) * (thisFT/thisFTA))) * 0.4 * thisFTA
                    TP = thisPoint / (thisFGA + thisFTA * 0.4 + thisTOV)
                    ORBW = 1 - thisORB/ (0.5 + (thisORB + thisDRB) * TP) / ( 0.25 + (1 - thisORB) * (0.4 + (thisORB + thisDRB)) * (1 - TP))
                    ORB_P = thisORB * TP * ORBW
                    SCP = (FG_P + AS_P + FT_P) * (1 - (thisORB/SP) * ORBW * TP) + ORB_P
                    FGxP = (thisFGA - thisFG) * (1 - 1.07 * ORBW)
                    FTxP = (1 - (thisFT/thisFTA) * (thisFT/thisFTA)) * 0.4 * thisFTA
                    TotalPo = SCP + FGxP + FTxP + thisTOV

                    PProd_FGP = 2 * (thisFG + 0.5 * thisFT) * (1 - 0.5 * (thisPoint - thisFT) / (2 * thisFGA) * AS)
                    PProd_ORBP = thisORB * ORBW * TP * (thisPoint/ (thisFG) + (1 - thisFT/thisFTA * thisFT/thisFTA))
                    PProd_ASTP = 2 * SCP * (1 - TP) * ((thisFG/thisFGA) + 0.5 * thisFT/thisFTA)
                    PProd = (PProd_FGP + PProd_ASTP + thisFT) * (1 - (thisORB/SCP) * ORBW * TP) + PProd_ORBP
                    ORRt = 100 * (PProd/ TotalPo)
            except Exception:
                pass        

        
reducer()