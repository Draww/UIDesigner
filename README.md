# UIDesigner
Create beautiful menus with buttons in Spigot easily!

## Usage
Please see upcoming YouTube videos for a complete tutorial.

Brief tutorial:
1. Set your plugin instance in the class UIDesignerAPI.
2. Create a new class extending MenuStandard
3. Create fields MenuButton that are created in the constructor.
4. Return your fields' item by overwritting getItemAt method in your menu class.
5. Show the menu to the player by calling Menu#displayTo method! 

## Installation
We use Maven to compile and so you need to, to use this library easily. See below for a step-by-step tutorial.

Notice: If you are having a builder Ant task, you should head over to /releases page (add it to the URL bar) to download the jar and install it as a plugin to have classes available for testing conditions. Otherwise, always shade the classes directly and ship them with your plugin.

Copyright: All Rights Reserved (C) 2018. Commercial and non-commercial use allowed as long as you provide a clear link on your (sales) page that you are using this library.  


1. Place this to your repositories:

		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>

2. Place this to your dependencies:

		<dependency>
			<groupId>com.github.kangarko</groupId>
			<artifactId>UIDesigner</artifactId>
			<version>master</version>
			<scope>compile</scope>
		</dependency>
    
2. Make sure that the library shades into your final .jar when you compile your plugin. Here is an example of a shade plugin that will do it for you:

IF YOU ALREADY HAVE A SHADE PLUGIN, ONLY USE THE RELOCATION SECTION FROM BELOW.

			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-shade-plugin</artifactId>
				<version>3.1.0</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>shade</goal>
						</goals>
					</execution>
				</executions>
				<configuration>
					<createDependencyReducedPom>false</createDependencyReducedPom>
					<relocations>
						<relocation>
							<pattern>me.kangarko.ui</pattern>
							<shadedPattern>${project.groupId}.ui</shadedPattern>
						</relocation>
					</relocations>
				</configuration>
			</plugin>
