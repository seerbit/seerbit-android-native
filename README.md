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
	        implementation 'com.github.seerbit:seerbit-android-native:1.0.2'
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
 
 class MainActivity : ComponentActivity(), ActionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SeerBitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    GoToPaymentGateway(actionListener=this)
                }
            }
        }
    }

    override fun onSuccess(data: Any?) {

    }

    override fun onClose() {
        Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show()
    }


}
 
 
@Composable
fun GoToPaymentGateway(context: Context = LocalContext.current, actionListener: ActionListener) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(20.dp)
    ) {

             val context: Context = LocalContext.current  // this applies if you are calling the sdk from a composable. you can obtain the context appropriately otherwise.
             val amount: String = "1700.00"
             val phone: String = "0809098****"
             val publicKey: String = "SMPPBK_SCAD2TXCYUUYYYTT17OTLECBGUAI"
             val fullName: String  = "SeerBit SeerBit"
             val email:String = "seerbitseerbit@gmail.com"
             var productId = "seerbit"
             var vendorId = "seerbit"
             var currency = "NGN" 
             var country = "NG"
             var pocketReference = "SBT-PBK_SCAD2T"
             var tokenize = false
             
             startSeerBitSDK(
                                context,
                                amount,
                                phoneNumber,
                                "SBPUBK_SCAD2TXCTYVZOORZEGXR17OTLECBGUAI",
                                fullName,
                                email,
                                actionListener = actionListener,
                                productDescription = productDescription,
                                productId = productId,
                                pocketReference = pocketReference,
                                transactionPaymentReference = "",
                                vendorId = vendorId,
                                country = country,
                                currency = currency,
                                tokenize = false
                            )
 }

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

