package com.mojang.metagun.entity


abstract class BossPart extends Entity {
    var dieIn = 0;

    def setRot(rot : Double) {
    }

    override def outOfBounds() {
    }  
}


