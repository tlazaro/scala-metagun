package com.mojang.metagun

object Stats {
  var instance = new Stats
  
  def reset() {
    instance = new Stats();
  }
}

class Stats {
  var deaths = 0;
  var shots = 0;
  var kills = 0;
  var jumps = 0;
  var time = 0;
  var hats = 0;
    
  def getSpeedScore() : Int = {
    val seconds = time/60;
    var speedScore = (60*10-seconds)*100;
    if (speedScore<0) speedScore = 0;
    speedScore;
  }

  def getDeathScore() : Int = {
    var deathScore = 10000-deaths*100;
    if (deathScore<0) deathScore = 0;
    deathScore
  }
    
  def getHatScore() : Int = {
    hats*5000
  }

  def getShotScore() : Int = {
    shots/10;
  }
    
  def getFinalScore() : Int = {
    return getSpeedScore()+getDeathScore()+getHatScore()+getShotScore();
  }
    
  def getTimeString() = {
    var seconds = time/60;
    val minutes = seconds/60;
    seconds%=60
    var str = minutes+":"
    if (seconds<10) str+="0"
    str += seconds
    str
  }
}
