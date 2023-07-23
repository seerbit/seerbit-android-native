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

For newer projects, add to the settings.gradle file instead

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```

- Add the dependency to your app level build.gradle file

```
dependencies {
	        implementation 'com.github.seerbit:seerbit-android-native:1.0.4'
	}
  
 ```
 
 ## Usage: These parameters must be supplied to the starting method;
 
 - Context //required
 - Merchant's live public key  //required
 - Amount  //required
 - Customer's full name  //required
 - Customer's email  //required
 - Customer's phone number  //required
 - productId // defaults to empty string
 - vendorId //defaults to empty string
 - currency //defaults to merchant's country currency
 - country //defaults to merchant's country
 - pocketReference // used when money is to be moved to pocket
 - transactionPaymentReference //we generate payment reference for each transaction, but if you supply yours, we will use it.
 - tokenize // used only when card is to be tokenized -  defaults to false
 - productDescription //defaults to empty string


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
             val productId : String  = "seerbit"
             val vendorId: String  = "seerbit"
             val currency: String  = "NGN" 
             val country: String  = "NG"
             val pocketReference: String  = "SBT-PBK_SCAD2T"
             val transactionPaymentReference: String  = "SBT-RTEUW_SCAD2T"
             val tokenize: Boolean = false
             val productDescription: String  = "seerbit"
             
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

## Properties:
| Property                    | type     | required  | default | Desc                                                                        |
|-----------------------------|----------|-----------|---------|-----------------------------------------------------------------------------|
| currency                    | String   | Optional  | NGN     | The currency for the transaction e.g NGN                                    |
| email                       | String   | Required  | None    | The email of the user to be charged                                         |
| publicKey                   | String   | Required  | None    | Your Public key or see above step to get yours                              |
| amount                      | String   | Required  | None    | The transaction amount                                                      |
| fullName                    | String   | Required  | None    | The fullname of the user to be charged                                      |
| phoneNumber                 | String   | Required  | None    | User phone Number                                                           |
| pocketReference             | String   | Optional  | None    | This is your pocket reference for vendors with pocket                       |
| vendorId                    | String   | Optional  | None    | This is the vendorId of your business using pocket                          |
| tokenize                    | bool     | Optional  | False   | Tokenize card                                                               |
| country                     | String   | Optional  | NG      | Transaction country which can be optional                                   |
| transactionPaymentReference | String   | Optional  | None    | Set a unique transaction reference for every transaction                    |
| productId                   | String   | Optional  | None    | This is the productId which is optional                                     |
| productDescription          | String   | Optional  | None    | The transaction description which is optional                               |
| actionListener              | Callback | Optional  | null    | actionListener which listens to if the transaction was successful or closed |
 
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

