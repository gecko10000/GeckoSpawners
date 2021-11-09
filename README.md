# GeckoSpawners

A spawner plugin like you've never seen before.
## About

This plugin allows spawners to spawn:
* all the entities you're used to
* exact-NBT entities
* items
* falling blocks (with block states and tile data included - you can rain down chests pre-filled with diamonds!)

Did you know that spawners can have *multiple* spawning candidates in one spawner? Bet you didn't!

You can add multiple different spawning candidates with differing weights to the spawner, meaning it can spawn multiple things at random!

## Usage

This plugin requires [Paper](https://papermc.io/) (or a fork) and [RedLib](https://github.com/Redempt/RedLib/releases).

To begin creating/editing spawners, enter the editor with `/geckospawners editor`.

There, you will see the list of spawners. Click the one at the bottom to create a new one.

Inside of each spawner, you will be able to edit the list of spawn candidates for the spawner, the name of the spawner, data values for the spawner (delay, range, spawn count, required player range, etc.) and the weight of the spawn candidates.

The plugin supports [MiniMessage](https://docs.adventure.kyori.net/minimessage#format), so use it to your heart's content.

Spawners are mineable and will maintain their information when picked up.

## Commands

`/geckospawners` (`/gs`):
* `edit` (`e`)
* `reload` (`r`)

## Permissions

* `geckospawners.silk` - permission required to mine spawners with silk touch
* `geckospawners.silk.bypass` - permission to bypass silk touch requirement for mining spawners - `default false`
* `geckospawners.command` - permission associated with the base command - grant it to tab-complete `/geckospawners`
* `geckospawners.edit` - permission for opening the editing GUI
* `geckospawners.reload` - permission to reload the configs

## Config

* `entity-removed-values` - the list of NBT tags to remove from entities when saving them to a spawner setup. `UUID` and `Pos` should not be removed from this list.
* `tile-entity-removed-values` - the list of NBT tags to remove from tile entities when saving them.
* `falling-block-time` - the value to set for falling blocks (default is 0, which makes the block disappear after one tick - not ideal).
* `center-falling-blocks` - centers falling blocks, useful if you want them to land and turn into blocks more often.
* `default-falling-blocks-dont-drop-items` - turns off falling blocks dropping items (not the item that spawns when breaking the block after it lands). This is useful when used with `center-falling-blocks` if you want blocks that can only be picked up, so players can't just make a floor out of torches and pick up the items directly.
* `fix-short-delay-on-spawner-place` - something I've found useful in my experience, as when spawners are placed, the delay is set to 1 second and allows a sort of exploitation in this way, placing and breaking over and over. This option will set the delay to the average of the minimum and maximum when the spawner is created.
* `spawner-mining-tools` - the list of materials that can be used to mine spawners (remember that you do need the proper permissions as well).
