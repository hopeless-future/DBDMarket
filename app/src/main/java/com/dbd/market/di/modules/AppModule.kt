package com.dbd.market.di.modules

import android.app.Application
import androidx.room.Room
import com.dbd.market.di.qualifiers.*
import com.dbd.market.helpers.operations.UserAddressesFirestoreOperations
import com.dbd.market.helpers.operations.UserCartProductsFirestoreOperations
import com.dbd.market.helpers.operations.UserUploadingImageFirebaseFirestoreOperation
import com.dbd.market.repositories.introduction.login.LoginRepository
import com.dbd.market.repositories.introduction.login.LoginRepositoryImplementation
import com.dbd.market.repositories.introduction.register.RegisterRepository
import com.dbd.market.repositories.introduction.register.RegisterRepositoryImplementation
import com.dbd.market.repositories.market.addresses.AddressesRepository
import com.dbd.market.repositories.market.addresses.AddressesRepositoryImplementation
import com.dbd.market.repositories.market.cart.CartProductsRepository
import com.dbd.market.repositories.market.cart.CartProductsRepositoryImplementation
import com.dbd.market.repositories.market.categories.headdress.HeaddressCategoryRepository
import com.dbd.market.repositories.market.categories.headdress.HeaddressCategoryRepositoryImplementation
import com.dbd.market.repositories.market.categories.legs.LegsCategoryRepository
import com.dbd.market.repositories.market.categories.legs.LegsCategoryRepositoryImplementation
import com.dbd.market.repositories.market.categories.main_category.MainCategoryRepository
import com.dbd.market.repositories.market.categories.main_category.MainCategoryRepositoryImplementation
import com.dbd.market.repositories.market.categories.suits.SuitsCategoryRepository
import com.dbd.market.repositories.market.categories.suits.SuitsCategoryRepositoryImplementation
import com.dbd.market.repositories.market.categories.torso.TorsoCategoryRepository
import com.dbd.market.repositories.market.categories.torso.TorsoCategoryRepositoryImplementation
import com.dbd.market.repositories.market.categories.weapons.WeaponsCategoryRepository
import com.dbd.market.repositories.market.categories.weapons.WeaponsCategoryRepositoryImplementation
import com.dbd.market.repositories.market.orders.OrdersRepository
import com.dbd.market.repositories.market.orders.OrdersRepositoryImplementation
import com.dbd.market.repositories.market.product_description.ProductDescriptionRepository
import com.dbd.market.repositories.market.product_description.ProductDescriptionRepositoryImplementation
import com.dbd.market.repositories.market.search.SearchRepository
import com.dbd.market.repositories.market.search.SearchRepositoryImplementation
import com.dbd.market.repositories.market.setup_order.SetupOrderRepository
import com.dbd.market.repositories.market.setup_order.SetupOrderRepositoryImplementation
import com.dbd.market.repositories.market.user.UserRepository
import com.dbd.market.repositories.market.user.UserRepositoryImplementation
import com.dbd.market.repositories.market.user_avatar.UserAvatarRepository
import com.dbd.market.repositories.market.user_avatar.UserAvatarRepositoryImplementation
import com.dbd.market.room.database.UserAvatarDatabase
import com.dbd.market.utils.Constants.FIREBASE_FIRESTORE_ADDRESSES_COLLECTION
import com.dbd.market.utils.Constants.FIREBASE_FIRESTORE_CART_PRODUCTS_COLLECTION
import com.dbd.market.utils.Constants.FIREBASE_FIRESTORE_ORDERS_COLLECTION
import com.dbd.market.utils.Constants.FIREBASE_FIRESTORE_PRODUCTS_COLLECTION
import com.dbd.market.utils.Constants.FIREBASE_FIRESTORE_USER_COLLECTION
import com.dbd.market.utils.Constants.FIREBASE_STORAGE_USERS_IMAGES_PATH
import com.dbd.market.utils.Constants.USER_AVATAR_DATABASE_NAME
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideRegisterRepository(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore): RegisterRepository = RegisterRepositoryImplementation(firebaseAuth, firebaseFirestore)

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    @Singleton
    fun provideUserUid() = FirebaseAuth.getInstance().currentUser?.uid

    @UsersCollectionReference
    @Provides
    @Singleton
    fun provideUsersCollectionReference() = Firebase.firestore.collection(FIREBASE_FIRESTORE_USER_COLLECTION)

    @Provides
    @Singleton
    fun provideUserDocumentReference(userUid: String?, @UsersCollectionReference usersCollectionReference: CollectionReference) = userUid?.let { usersCollectionReference.document(it) }

    @Provides
    @Singleton
    fun provideCartProductsFirestoreOperations(@UserCartProductsCollectionReference cartProductsCollectionReference: CollectionReference?, firebaseFirestore: FirebaseFirestore) = UserCartProductsFirestoreOperations(cartProductsCollectionReference, firebaseFirestore)

    @Provides
    @Singleton
    fun provideAddressesFirestoreOperations(@UserAddressesCollectionReference userAddressesCollectionReference: CollectionReference?) = UserAddressesFirestoreOperations(userAddressesCollectionReference)

    @Provides
    @Singleton
    fun provideUploadingImageFirebaseFirestoreOperation(userDocumentReference: DocumentReference?) = UserUploadingImageFirebaseFirestoreOperation(userDocumentReference)

    @UserCartProductsCollectionReference
    @Provides
    @Singleton
    fun provideUserCartProductsCollectionReference(userDocumentReference: DocumentReference?) = userDocumentReference?.collection(FIREBASE_FIRESTORE_CART_PRODUCTS_COLLECTION)

    @UserAddressesCollectionReference
    @Provides
    @Singleton
    fun provideUserAddressesCollectionReference(userDocumentReference: DocumentReference?) = userDocumentReference?.collection(FIREBASE_FIRESTORE_ADDRESSES_COLLECTION)

    @UserOrderCollectionReference
    @Provides
    @Singleton
    fun provideUserOrderCollectionReference(userDocumentReference: DocumentReference?) = userDocumentReference?.collection(FIREBASE_FIRESTORE_ORDERS_COLLECTION)

    @Provides
    @Singleton
    fun provideUserRepository(userDocumentReference: DocumentReference?, @UserOrderCollectionReference userOrderCollectionReference: CollectionReference?, @FirebaseStorageReferenceUserImages storageReference: StorageReference, firebaseAuth: FirebaseAuth, userAvatarDatabase: UserAvatarDatabase): UserRepository = UserRepositoryImplementation(userDocumentReference, userOrderCollectionReference, storageReference, firebaseAuth, userAvatarDatabase)

    @Provides
    @Singleton
    fun provideAddressesRepository(@UserAddressesCollectionReference userAddressesCollectionReference: CollectionReference?): AddressesRepository = AddressesRepositoryImplementation(userAddressesCollectionReference)

    @Provides
    @Singleton
    fun provideCartProductsRepository(@UserCartProductsCollectionReference cartProductsCollectionReference: CollectionReference?): CartProductsRepository = CartProductsRepositoryImplementation(cartProductsCollectionReference)

    @Provides
    @Singleton
    fun provideProductDescriptionRepository(@UserCartProductsCollectionReference cartProductsCollectionReference: CollectionReference?, userCartProductsFirestoreOperations: UserCartProductsFirestoreOperations): ProductDescriptionRepository = ProductDescriptionRepositoryImplementation(cartProductsCollectionReference, userCartProductsFirestoreOperations)

    @Provides
    @Singleton
    fun provideSetupOrderRepository(@UserAddressesCollectionReference userAddressesCollectionReference: CollectionReference?, @UserCartProductsCollectionReference userCartProductsCollectionReference: CollectionReference?, @UserOrderCollectionReference userOrderCollectionReference: CollectionReference?, firebaseFirestore: FirebaseFirestore): SetupOrderRepository = SetupOrderRepositoryImplementation(userAddressesCollectionReference, userCartProductsCollectionReference, userOrderCollectionReference, firebaseFirestore)

    @Provides
    @Singleton
    fun provideOrdersRepository(@UserOrderCollectionReference userOrderCollectionReference: CollectionReference?): OrdersRepository = OrdersRepositoryImplementation(userOrderCollectionReference)

    @ProductsCollectionReference
    @Provides
    @Singleton
    fun provideProductsCollectionReference() = Firebase.firestore.collection(FIREBASE_FIRESTORE_PRODUCTS_COLLECTION)

    @Provides
    @Singleton
    fun provideLoginRepository(firebaseAuth: FirebaseAuth, @UsersCollectionReference usersCollectionReference: CollectionReference): LoginRepository = LoginRepositoryImplementation(firebaseAuth, usersCollectionReference)

    @FirebaseStorageReference
    @Provides
    @Singleton
    fun provideFirebaseStorage() = Firebase.storage.reference

    @FirebaseStorageReferenceUserImages
    @Provides
    @Singleton
    fun provideFirebaseStorageReferenceUserImages(userUid: String?, @FirebaseStorageReference storageReference: StorageReference) = storageReference.child(FIREBASE_STORAGE_USERS_IMAGES_PATH).child(userUid!!)

    @Provides
    @Singleton
    fun provideMainCategoryRepository(firebaseFirestore: FirebaseFirestore): MainCategoryRepository = MainCategoryRepositoryImplementation(firebaseFirestore)

    @Provides
    @Singleton
    fun provideSuitsCategoryRepository(@ProductsCollectionReference productsCollectionReference: CollectionReference): SuitsCategoryRepository = SuitsCategoryRepositoryImplementation(productsCollectionReference)

    @Provides
    @Singleton
    fun provideWeaponsCategoryRepository(@ProductsCollectionReference productsCollectionReference: CollectionReference): WeaponsCategoryRepository = WeaponsCategoryRepositoryImplementation(productsCollectionReference)

    @Provides
    @Singleton
    fun provideHeaddressCategoryRepository(@ProductsCollectionReference productsCollectionReference: CollectionReference): HeaddressCategoryRepository = HeaddressCategoryRepositoryImplementation(productsCollectionReference)

    @Provides
    @Singleton
    fun provideTorsoCategoryRepository(@ProductsCollectionReference productsCollectionReference: CollectionReference): TorsoCategoryRepository = TorsoCategoryRepositoryImplementation(productsCollectionReference)

    @Provides
    @Singleton
    fun provideLegsCategoryRepository(@ProductsCollectionReference productsCollectionReference: CollectionReference): LegsCategoryRepository = LegsCategoryRepositoryImplementation(productsCollectionReference)

    @Provides
    @Singleton
    fun provideUserAvatarDatabase(context: Application) = Room.databaseBuilder(context, UserAvatarDatabase::class.java, USER_AVATAR_DATABASE_NAME).build()

    @Provides
    @Singleton
    fun provideUserAvatarRepository(userAvatarDatabase: UserAvatarDatabase): UserAvatarRepository = UserAvatarRepositoryImplementation(userAvatarDatabase)

    @Provides
    @Singleton
    fun provideSearchRepository(@ProductsCollectionReference productsCollectionReference: CollectionReference): SearchRepository = SearchRepositoryImplementation(productsCollectionReference)
}