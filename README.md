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

### 📋 Requirements

#### 🔧 Required Plugins
LuckPerms
PlaceholderAPI

#### 🗄️ Database
MySQL 5.7+

#### 🎮 Minecraft Versions
Primary Support: 1.21 - 1.21.8

Planned Support: 1.12.2 - 1.21.8 (Coming Soon!)

### 🚀 Installation
Download the latest version from Releases

Place the JAR file in your plugins/ folder

Restart your server

Configure the config.yml with your database settings

Reload with /priv reload or restart server

### 📦 Database Setup

``` sql
CREATE DATABASE minecraft;
CREATE USER 'minecraft'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON minecraft.* TO 'minecraft'@'localhost';
FLUSH PRIVILEGES;
```

### 📊 Support
#### ❓ Need Help?
Discord: borichdev (Direct message)

GitHub Issues: Create Issue

Wiki: Documentation

#### 🐛 Reporting Bugs
Please include:

Minecraft version

Plugin version

Error logs

Steps to reproduce

### 🌟 Contributing
We welcome contributions! Feel free to:

#### 🐛 Report bugs

#### 💡 Suggest features

#### 🔧 Submit pull requests

#### 📖 Improve documentation

### 📄 License
See the LICENSE file for details.

### 🙏 Acknowledgments
LuckPerms Team - For excellent permissions system

PlaceholderAPI Team - For robust placeholder system

MySQL - For reliable database storage

All Contributors - Thank you for your support!



From borich with luv ❤️
