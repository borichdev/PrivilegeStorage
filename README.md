# PrivilegeStorage 🗃️

[![GitHub release](https://img.shields.io/github/v/release/borichdev/PrivilegeStorage?style=for-the-badge)](https://github.com/borichdev/PrivilegeStorage/releases)
[![Discord](https://img.shields.io/discord/your-server-id?color=7289DA&label=Discord&logo=discord&style=for-the-badge)](https://discord.gg/your-invite)
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
| Command | Description | Permission |
|---------|-------------|------------|
| `/priv help` | Show help menu | `privilegestorage.help` |
| `/priv list` | View your privileges | `privilegestorage.list` |
| `/priv list <player>` | View another player's privileges | `privilegestorage.list.others` |
| `/priv give <player> <privilege>` | Give privilege to another player | `privilegestorage.give` |
| `/priv set <player> <privilege> <amount>` | Set privilege amount | `privilegestorage.admin` |
| `/priv add <player> <privilege> <amount>` | Add privileges | `privilegestorage.admin` |
| `/priv take <player> <privilege> <amount>` | Take privileges | `privilegestorage.admin` |
| `/priv reset <player>` | Reset player's privileges | `privilegestorage.admin` |
| `/priv reload` | Reload configuration | `privilegestorage.admin` |

### 📊 Placeholders
- `%privilege_highest%` - Shows player's highest privilege
- `%privilege_<privilege>%` - Shows amount of specific privilege
- *Example: `%privilege_vip%` shows VIP privilege count*

### ⚙️ Configuration
```yaml
# Database Settings
database:
  host: "localhost"
  port: 3306
  database: "minecraft"
  username: "root"
  password: "password"
  table-prefix: "priv_"
  
# Plugin Settings
settings:
  update-interval: 300
  default-privileges:
    - "default: 1"
  
# Messages (Fully customizable)
messages:
  no-permission: "&cYou don't have permission!"
  privilege-given: "&aSuccessfully given {privilege} to {player}"
