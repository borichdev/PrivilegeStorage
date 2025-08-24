# PrivilegeStorage 🗃️

[![GitHub release](https://img.shields.io/github/v/release/borichdev/PrivilegeStorage?style=for-the-badge)](https://github.com/borichdev/PrivilegeStorage/releases)
[![Discord](https://img.shields.io/discord/your-server-id?color=7289DA&label=Discord&logo=discord&style=for-the-badge)](https://discord.gg/PWp3Et5U)
[![MySQL](https://img.shields.io/badge/MySQL-Supported-orange?style=for-the-badge&logo=mysql)](https://mysql.com)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21-green?style=for-the-badge&logo=minecraft)](https://minecraft.net)

> **The Ultimate Solution for Privilege Management on Minecraft Networks** 🌟

A sophisticated Minecraft plugin designed to solve privilege storage and management issues across single servers and complex network setups. Never lose privileges again!

---

## ✨ Features

### 🎯 Core Functionality
- **Secure Privilege Storage** - Store player privileges in MySQL database
- **Cross-Server Compatibility** - Works perfectly on both single servers and networks
- **Privilege Transfer** - Players can share privileges with each other
- **Admin Management** - Comprehensive admin tools for privilege control

### 🛠️ Commands
| Command | Description |
|---------|-------------|
| `/priv help` | Show help menu |
| `/priv list` | View your privileges |
| `/priv list <player>` | View another player's privileges |
| `/priv give <player> <privilege>` | Give privilege to another player |
| `/priv set <player> <privilege> <amount>` | Set privilege amount |
| `/priv add <player> <privilege> <amount>` | Add privileges |
| `/priv take <player> <privilege> <amount>` | Take privileges |
| `/priv reset <player>` | Reset player's privileges |
| `/priv reload` | Reload configuration |

### 📊 Placeholders
- `%privilege_highest%` - Shows player's highest privilege
- `%privilege_<privilege>%` - Shows amount of specific privilege
  *Example: `%privilege_vip%` shows VIP privilege count*
