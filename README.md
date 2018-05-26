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
For the time being, you need to download this repository and shade it with your plugin so that our classes are within your plugin's jar. Here's how to do it:

1. Download this repository.
2. Import it as a Maven project.
3. Place this to your dependencies inside your plugin's pom.xml:

		<dependency>
			<groupId>me.kangarko.ui</groupId>
			<artifactId>UIDesigner</artifactId>
			<version>1.0.0</version>
			<scope>compile</scope>
		</dependency>
    
4. Make sure that the library shades into your final .jar when you compile your plugin. Here is an example of a shade plugin that will do it for you:

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
