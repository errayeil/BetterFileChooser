# BetterFileChooser
Provides a more robust FileChooser compared to the included JFileChooser in the swing packages.

The goals of BetterFileChooser is to:
- Provide a customizable but familiar interface.
- Provide robust and functional Swing based components.
  - Each custom component is usable separately from the BetterFileChooser class.
- Extend the capabilities of file choosing UI.
- Provide a means to keep certain data consistent accross instances of BetterFileChooser. For example: 
  - The BetterFileChooser dialog position, size, and currently selected directory will be remembered and re-applied when it is used again.
    - This can be shut off , if required.
- In the end with everything above included, a useful, fluid, and functional interface for the end-user.

# Some API notes
Besides the BetterFileChooser and BetterComponent's that are useable within the library, it also provides an API called Registry to register data to a store off-memory. Utilizing the Registry is how various variables remain consistent across instances of the UI. Currently, it only supports the built in components but I have plans to make it available and easy to use for anything else. Essentially, it'll end up plug and play. It's basically a Preferences wrapper to store this data in an organized way.

# Current progress
Right now BetterFileChooser is in a heavily incomplete state, with two UI components mostly completed. They can be used
as is now with little bugs or issues, but I would recommend not utilizing this library in any of your projects until it is 
mostly completed. The current task list is as follows:

- [ ] Complete BetterFileList
- [ ] Complete DriveRootTree
- [ ] Complete FileChooserUI ( This one is pretty hefty, lots of misc. components to work on )
- [ ] Complete BetterFileChooser

One those four tasks are complete I will move onto integrating other components I have planned.


# Usage
Using BetterFileChooser is as simple as using JFileChooser:

```java

BetterFileChooser chooser = new BetterFileChooser ( );

chooser.setDialogTitle ( "Hello there." );
chooser.setCurrentDirectory ( "path-to-ilum" );
chooser.setViewMode ( BetterFileChooser.FOLDERS_ONLY );

if (chooser.showOpenDialog ( null ) == BetterFileChooser.APPROVE_OPTION ) {
    //Do stuff with selected file (s)
}
```

# Other Usages
If you want to use the other components included in the library, they're easy to use as well and require nothing special.

```java

/**
 * A file tree designed to explore all of the storage drives in the computer.
 */
DriveRootTree tree = new DriveRootTree ( );

tree.setExpandable ( false); //This makes it so the storage drive tree nodes cannot be expanded!
tree.setShowHidden ( true ); //Hidden files will now visible within the tree.
tree.setViewMode ( BetterFileChooser.FOLDERS_AND_FILES ); //Both folders and files will be visible in the tree.
tree.setUnloadOnCollapse ( true ); //Any collapsed nodes with children will have those children unloaded.
```

# Dependencies
Currently, BetterFileChooser makes use of these dependencies:
- [JInputValidator](https://github.com/rhwood/jinputvalidator)
- [PanelMatic](https://github.com/codeworth-gh/PanelMatic)

# Other notes
This is a hobby project of mine, and I actually do this for fun. I don't do this as a profession. With that said, as I
am not experienced and did not go to school for this, there may be some things that are questionable or do not make sense. 
If you notice anything, please feel free to reach out and share what you've noticed. I'm doing this to learn more and I 
don't always notice my mistakes. In any case, if you find this library useful or inspirational I'd love to hear about it. :)
