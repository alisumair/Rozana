# Rozana – روزانہ
### All-in-One Daily Life App for Pakistan

**Rozana (روزانہ)** is a native Android application built specifically for people in Pakistan to manage daily financial expenses, incomes, utility bills, everyday tasks, document expiries, and instant calculations in one modern, elegant, and simple interface.

---

## 🌟 Key Features

1. **Dashboard & Summary:**
   - Greeting & today's date in English and Urdu.
   - Today's Income, Today's Expenses, and Remaining Balance calculated accurately.
   - Monthly budget progress bar with warning alerts at 80% and over-budget limits.
   - Quick action shortcuts for instant logging.

2. **Income & Expense Management:**
   - Add, edit, delete, and view transactions.
   - Pakistani categories: Salary, Business, Delivery work, Freelancing, Ration & Food, Petrol/Fuel, Electricity, Gas, Internet, Rent, Medical, etc.
   - Filter by All, Income, or Expense.

3. **Bill Reminders:**
   - Track utility bills: Electricity (LESCO, K-Electric, FESCO), Sui Gas (SNGPL, SSGC), Fiber Internet, Mobile, Rent, School Fees.
   - Status tracking (Paid / Unpaid) with one-tap "Mark Paid".
   - Local Android notification alerts.

4. **Daily Task Manager:**
   - Priority-based task tracking with completion checkboxes.
   - Time and note management.

5. **Document Expiry Reminders:**
   - Track NADRA Smart CNIC, Driving License, Vehicle/Bike Registration, and Passport expiry dates.
   - Countdown timer with urgent badges within 30 days of expiry.

6. **Daily Offline Calculator:**
   - Built-in calculator with +, -, ×, ÷, %, +/-, Clear, and Backspace.
   - One-tap "Add as Expense" or "Add as Income" to immediately log calculation results into accounts!

7. **Smart Assistant (Urdu, Roman Urdu & English):**
   - Natural query support:
     - *"Aj kitny paisay kharch kiye?"*
     - *"Main ne aj kitni income ki?"*
     - *"Mera budget kitna baqi hai?"*
     - *"Mere bills kab due hain?"*
   - Powered by user's actual saved records without inventing fake data.

8. **Voice & Natural Text Input:**
   - Speech-to-text parser (e.g., *"Aj 1500 rupay kamaye aur 300 rupay petrol par kharch kiye"*).
   - Confirmation dialog guarantees transactions are verified before saving.

9. **Pakistan Green & White Theme + Urdu RTL Support:**
   - Official Pakistani deep green and crisp white design system.
   - Full Right-to-Left (RTL) support when Urdu language is active.
   - Light and Dark modes.

---

## 🛠️ Tech Stack

- **Platform:** Android (minSdk 24, targetSdk 36)
- **Language:** Kotlin 2.2
- **UI Framework:** Jetpack Compose + Material 3
- **Architecture:** MVVM (Model-View-ViewModel) + Clean Repository Pattern
- **Local Storage:** Room Database (100% offline-first)
- **Concurrency:** Kotlin Coroutines & Flow

---

## 🚀 How to Push to GitHub

### Option 1: Direct from Google AI Studio UI (Easiest)
1. In the Google AI Studio interface, open the **Export / Share** menu in the top bar.
2. Click **Push to GitHub** (or **Export to GitHub**).
3. Connect your GitHub account and specify your repository name (`rozana`).

### Option 2: Using Git Terminal / Command Line
If you downloaded the ZIP file:
```bash
git init
git add .
git commit -m "Initial commit of Rozana app"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/rozana.git
git push -u origin main
```
