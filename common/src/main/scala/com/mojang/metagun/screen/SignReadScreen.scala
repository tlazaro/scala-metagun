package com.mojang.metagun.screen;

import com.mojang.metagun.Art;
import com.mojang.metagun.Input;

class SignReadScreen(var parent : Screen, var id : Int) extends Screen {
    private val signs = Array[Array[String]](
            Array(
                "READING",
                "", 
                "PRESS UP TO READ SIGNS"
            ),
            Array(
                "JUMPING",
                "", 
                "PRESS Z TO JUMP",
                "YOU CAN JUMP HIGHER BY",
                "GETTING A RUNNING START",
                "OR HOLDING DOWN Z"
            ),
            Array(
                "PROGRESSING",
                "", 
                "LEAVE A ROOM THROUGH ANY",
                "EXIT TO CONTINUE YOUR",
                "ADVENTURE"
            ),
            Array(
                "DYING",
                "", 
                "IF YOU DIE, YOU RESTART",
                "AT THE BEGINNING OF THE",
                "CURRENT ROOM"
            ),
            Array(
                "DODGING",
                "", 
                "THE GUNNERS DON'T LIKE YOU",
                "AND SHOOT AT YOU.",
                "IT WOULD BE WISE TO STAY AWAY"
            ),    
            Array(
                "THE LAUNCHER",
                "", 
                "AS YOU PICK UP THE LAUNCHER,",
                "YOU REALIZE IT'S NOT YOUR",
                "AVERAGE LAUNCHER.",
                "",
                "PRESS UP AND DOWN TO AIM",
                "PRESS X TO FIRE THE LAUNCHER"
            ),      
            Array(
                "JONESING",
                "", 
                "DON'T FORGET YOUR FEDORA!"
            ),
            Array(
                "EXPLODING",
                "", 
                "TNT BLOCKS ARE HIGHLY",
                "EXPLOSIVE, AND WILL",
                "REACT POORLY TO BEING",
                "SHOT."
            ),              
            Array(
                "PUSHING",
                "", 
                "THE CAMARADERIE BOX IS",
                "SOMETHING SOMETHING",
                "",
                "IT'S FROM PORTAL."
            ),              
            Array(
                "BATTLING",
                "", 
                "THE GREMLIN IS LARGE",
                "AND IN YOUR WAY.",
                "OVERHEAT IT TO DESTROY",
                "IT AND CLAIM YOUR PRIZE"
            ),      
            Array(
                "EVADING",
                "", 
                "THE GUNNERS SHOTS WILL",
                "PASS THROUGH GLASS.",
                "YOU, HOWEVER, WILL NOT"
            ),         
            Array(
                "SWEATING",
                "", 
                "THESE SLIGHTLY MORE",
                "SOPHISTICATED GREMLINS",
                "HAVE LEARNED A NEW",
                "TRICK"
            ),
            Array(
                "CONVEYING",
                "", 
                "TIME TO BURN OFF SOME",
                "FAT AND HAVE FUN WHILE",
                "DOING IT!"
            ),          
            Array(
                "BOSSFIGHTING",
                "", 
                "BEHIND THIS DOOR, MEGAN",
                "AWAITS! WHO IS MEGAN?",
                "ARE YOU MEGAN?"
            ),            
            Array(
                "THE NEW LAUNCHER",
                "",
                "WELL, THIS IS BAD."
            ),               
            Array(
                "FEEDING",
                "",
                "THE JABBERWOCKY IS",
                "HUNGRY, AND WILL EAT",
                "WAY MORE THAN IT SHOULD",
                "",
                "PLEASE DO NOT FEED!"
            ),               
            Array(
                "HOVERING",
                "",
                "THE RECOIL ON THE NEW",
                "LAUNCHER SURE IS",
                "POWERFUL!"
            ),
            Array(
                "FLYING",
                "",
                "SERIOUSLY, THE RECOIL",
                "IS OUT OF THIS WORLD!"
            ),             
            Array(
                "WINNING",
                "",
                "YOUR FINAL CHALLENGE",
                "IS RIGHT DOWN THIS",
                "HALLWAY."
            ), 
            Array(
                "FRESHERERST",
                "",
                "BIG ADAM, GIANT SISTER.",
                "IT IS KNOWN BY MANY NAMES",
                "BUT JUDITH 4HRPG BLUEBERRY.",
                "",
                "FISSION MAILED!"
            )
    )
    
    private var delay = 15;
    
    def render() {
        parent.render();
        spriteBatch.begin();
        var xs = 0;
        var ys = signs(id).length+3;
        for (y <- 0 until signs(id).length) {
            val s = signs(id)(y).length();
            if (s>xs) xs = s;
        }
        val xp = 160-xs*3;
        val yp = 120-ys*3;
        var x = -1
        while(x<xs+1) {
            var y = -1
            while(y < ys+1) {
                var xf = 1;
                var yf = 12;
                if (x<0) xf-=1;
                if (y<0) yf-=1;
                if (x>=xs) xf+=1;
                if (y>=ys) yf+=1;
                draw(Art.guys(xf)(yf), xp+x*6, yp+y*6);
                y+=1
            }
            x += 1
        }
        
        for (y <- 0 until signs(id).length) {
            drawString(signs(id)(y), xp, yp+y*6);
        }
        if (delay==0)
        drawString("PRESS X", xp+(xs-8)*6, yp+(signs(id).length+2)*6);
        spriteBatch.end();
    }
    
    override def tick(input : Input) {
        if (!input.oldButtons(Input.ESCAPE) && input.buttons(Input.ESCAPE)) {
            setScreen(parent);
            return;
        }
        if (delay>0) delay-=1;
        if (delay==0 && input.buttons(Input.SHOOT) && !input.oldButtons(Input.SHOOT)) {
            setScreen(parent);
        }
    }    
}
