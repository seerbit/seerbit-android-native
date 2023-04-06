![seerbit](https://user-images.githubusercontent.com/74198009/230321289-beb6c9ec-6d29-4d79-84cb-abb0606a23ab.png)


                                                      


 # SeerBit Native Android SDK
 
SeerBit Native Android sdk is used to seamlessly integrate SeerBit payment gateways into Native android applications. It is very simple and easy to integrate.

## Requirements:

- First the merchant must have an account with SeerBit or create one on [SeerBit Merchant Dashboard](https://www.dashboard.seerbitapi.com/#/auth/login) to get started.
- Obtain the live public key of the merchant.

## Implementation:

- Add the below code to your root build.gradle file

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}  
```

- Add the dependency to your app level build.gradle file

```
dependencies {
	        implementation 'com.github.seerbit:seerbit-android-native:1.0.1'
	}
  
 ```
 
 ## Usage: These parameters must be supplied to the starting method;
 
 - Context
 - Merchant's live public key
 - Amount
 - Customer's full name
 - Customer's email
 - Customer's phone number
 
 Supply these parameters to the starting method, `startSeerBitSDK()`. For example,
 
 ```
 val context: Context = LocalContext.current  // this applies if you are calling the sdk from a composable. you can obtain the context appropriately otherwise.
 val amount: String = "1700.00"
 val phone: String = "0809098****"
 val publicKey: String = "SMPPBK_SCAD2TXCYUUYYYTT17OTLECBGUAI"
 val fullName: String  = "SeerBit SeerBit"
 val email:String = "seerbitseerbit@gmail.com"
 
 startSeerBitSDK (
                    context = context,
                    amount = amount,
                    phoneNumber = phone,
                    publicKey =  publicKey,
                    fullName =  fullName,
                    email =  email
                 )
 ```
 
 Calling this method with the parameters will initiate the sdk.
 
 ## Support:
 
 If you encounter any problems, or you have questions or suggestions, create an issue here or send your inquiry to developers@seerbit.com
 
 
 ## Contributors:
 
 
<a href = "https://github.com/seerbit/seerbit-android-native">
  <img src = "https://contrib.rocks/image?repo = https://github.com/sir-miracle"/>
  <img src = "https://contrib.rocks/image?repo = https://github.com/AdeifeTaiwo"/>
  <img src = "https://contrib.rocks/image?repo = https://github.com/victorighalo"/>
  <img src = "https://contrib.rocks/image?repo = https://github.com/amoskeyz"/>
  <img src = "https://contrib.rocks/image?repo = https://github.com/elozino1"/>
</a>

