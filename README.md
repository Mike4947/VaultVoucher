# VaultVoucher

A simple, lightweight plugin that allows players to convert their in-game currency into physical, tradable voucher items.

![image](https://github.com/user-attachments/assets/c5c56b0c-b7b7-4cdf-9472-e16cc3a2d782)

*(A voucher item held in-game showing its name and lore)*

<img src="https://img.shields.io/github/downloads/Mike4947/vaultvoucher/total?style=for-the-badge&color=2196F3" alt="Total Downloads"/>
---

## ❗ Important: Dependencies
This plugin **WILL NOT** load or function correctly without the following two plugins installed on your server:

1.  ### [**Vault**](https://www.spigotmc.org/resources/vault.34315/)
    This is required to handle the connection to your server's economy.

2.  ### [**EssentialsX**](https://essentialsx.net/downloads.html) (or any other Vault-compatible economy plugin)
    This is required to be the "bank" that actually manages the player balances. EssentialsX is needed for its stability and ease of use.

---

## Features
-   **Withdraw Money:** Convert your balance into a physical item using a simple command.
-   **Redeem Vouchers:** Redeem a voucher's value back into your account with a simple right-click.
-   **Safe Trading:** Trade vouchers with other players safely, avoiding scams.
-   **Shop Integration:** Use vouchers as currency for other plugins, such as Shopkeepers.

## Commands & Permissions

| Command | Description | Permission |
| :--- | :--- | :--- |
| `/voucher <amount>` | Creates a voucher by withdrawing money from your account. | `vaultvoucher.create` |

| Permission | Description | Default |
| :--- | :--- | :--- |
| `vaultvoucher.create` | Allows a player to use the `/voucher` command. | `true` |
| `vaultvoucher.redeem` | Allows a player to right-click a voucher to redeem it. | `true` |

## How It Works

1.  **Creating a Voucher:** A player with `$1500` types `/voucher 1500`. The money is taken from their account, and they are given a paper voucher item worth `$1500`.

2.  **Redeeming a Voucher:** The player (or anyone they trade it to) can right-click the voucher item to instantly deposit its value into their account. The voucher is consumed in the process.
---
### Images
---
![image](https://github.com/user-attachments/assets/47b50cfd-66f1-4d4d-b437-5dbfc6c527a0)
---
![image](https://github.com/user-attachments/assets/139c35dd-b33a-46cd-8edc-2bce4717111d)
---
![image](https://github.com/user-attachments/assets/728ada86-3b48-4119-8749-371ff583e718)
---
![image](https://github.com/user-attachments/assets/323b1018-3ae1-426a-ab88-1f59edafb07d)
