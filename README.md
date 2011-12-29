Scala Libgdx Metagun Implementation
-------------------------------------------------------------------------------

This is a port to Scala from the Java example provided with Libgdx. I claim no right whatsoever of the game, it was developed by Notch.

The code conversion was done quickly and naively without caring much for the 'Scala way' or optimizing the code. I just went on and converted line by line without thinking much except for a few cases. I plan on re-working it now that I finished porting it to 100% Scala code.

The desktop build is working. Android or Web are not done yet but the project is setup to allow independent development from the build by having a "common" project.

Help is welcomed!

Setup SBT
-------------------------------------------------------------------------------

* Make sure you have Scala and SBT 0.11.2 working

Checkout this repository and go the the root folder from the command line.

In the command-line:

    $ sbt
    ...
    > project desktop
    ...
    > run

And the game should be running...

Setup Netbeans
-------------------------------------------------------------------------------

* Make sure you have Scala support working properly in Netbeans 7.0 (older versions work fine too but those do not have Git support ;))

Using Netbeans open the scala project in the common folder. All sources were added to that project and it is ready to be run (the Desktop build main file will be executed)

Usage
-------------------------------------------------------------------------------

I do the development using Netbeans but I compile and run with SBT from the command-line.
