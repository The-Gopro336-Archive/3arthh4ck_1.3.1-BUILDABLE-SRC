# Where is the src?
Download here: [colorblindness/3arthh4ck](https://github.com/colorblindness/3arthh4ck)

I was going to create another reconstructed, buildable SRC here, but then the real one was released by the devs. I had that one posted here earlier, but I have decided to remove it from this repo. It was created and uploaded by ohare, and he should get the credit for it. It sucks to have some unaffiliated person take most of the credit for something you created and released so thats why I have removed it. At the time, ohares repo was not fully buildable, but I see that has been fixed now.

I can personally comfirm that ohares repo, and every other earthhack 1.3.1 version realeased by phobos devs, and users, to be 100% clean.

some people believe a byte array in kitcommand is malicuous, but I assure you it is not. It is just [nbt data](https://github.com/Gopro336/3arthh4ck_1.3.1-BUILDABLE-SRC/issues/25)

# Building Earthh4ack
this time, when you downlod the repo, you CANNOT download the zip, as github strips the .git folder, which is needed for it to compile properly

Alternitvely, you can use [my fork](https://github.com/Gopro336/3arthh4ck), that has these issues resolved

![Pic](https://github.com/Gopro336/3arthh4ck_1.3.1-BUILDABLE-SRC/blob/info/dontDownloadZip.png)

You instead have to clone the repo. This can be done using git, the github desktop app, or cloneing into an IDE such as intelij.

The easiest method to build earthhack is to clone into an Intelij project, and build

![Pic](https://github.com/Gopro336/3arthh4ck_1.3.1-BUILDABLE-SRC/blob/info/intelij.png)

Once in intelij wait for it to index, and then open up the gradle tab on the side. Click setupDecompWorkspace first, and then click build

![Pic](https://github.com/Gopro336/3arthh4ck_1.3.1-BUILDABLE-SRC/blob/info/intelijBuild.png)


Build progress will show in the build tab at the bottom of the screen. Once you have run setupDecompWorkspace once, you do not have to run it again. When the build has completed, navagate to the new build folder that has been created. build/libs is where you will find 2 jar files. ONLY use the jar with the RELEASE label at the end of its name.

Hope this helps!!
