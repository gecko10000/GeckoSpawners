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

Inside of each spawner, you will be able to edit the list of spawn candidates for the spawner, the name of the spawner, and the weight of the spawn candidates.

The plugin supports [MiniMessage](https://docs.adventure.kyori.net/minimessage#format), so use it to your heart's content.

## Commands

`/geckospawners` (`/gs`):
* `edit` (`e`)
* `reload` (`r`)

## Permissions

* `geckospawners.command` - permission associated with the base command - grant it to tab-complete `/geckospawners`
* `geckospawners.edit` - permission for opening the editing GUI
* `geckospawners.reload` - permission to reload the configs
