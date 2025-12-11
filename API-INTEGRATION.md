# 🎨 Guía de Integración - Frontend y Pagos

Esta guía documenta la integración completa del backend con Angular y MercadoPago.

---

## 📋 Tabla de Contenidos

1. [Servicios Angular](#-servicios-angular)
2. [Flujo de Carrito](#-flujo-de-carrito)
3. [Flujo de Checkout](#-flujo-de-checkout)
4. [Integración MercadoPago](#-integración-mercadopago)
5. [Webhooks](#-webhooks)
6. [Ejemplos Completos](#-ejemplos-completos)

---

## 🔧 Servicios Angular

### 1. Auth Service

```typescript
// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  dni: string;
  birthDate: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  user: {
    id: number;
    firstName: string;
    lastName: string;
    email: string;
    roles: string[];
  };
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/api/auth`;
  private currentUserSubject = new BehaviorSubject<any>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Cargar usuario desde localStorage al iniciar
    const storedUser = localStorage.getItem('currentUser');
    if (storedUser) {
      this.currentUserSubject.next(JSON.parse(storedUser));
    }
  }

  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, data)
      .pipe(map(response => {
        this.setSession(response);
        return response;
      }));
  }

  login(data: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, data)
      .pipe(map(response => {
        this.setSession(response);
        return response;
      }));
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  isAdmin(): boolean {
    const user = this.currentUserSubject.value;
    return user?.roles?.includes('ROLE_ADMIN') || false;
  }

  private setSession(authResponse: AuthResponse): void {
    localStorage.setItem('token', authResponse.token);
    localStorage.setItem('currentUser', JSON.stringify(authResponse.user));
    this.currentUserSubject.next(authResponse.user);
  }
}
```

### 2. Address Service

```typescript
// src/app/services/address.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface Address {
  id?: number;
  cityId: number;
  districtId: number;
  street: string;
  number: string;
  phone: string;
  reference?: string;
  label: string;
  isDefault: boolean;
  cityName?: string;
  districtName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface City {
  ciudadId: number;
  nombre: string;
}

export interface District {
  distritoId: number;
  ciudadId: number;
  nombre: string;
}

@Injectable({
  providedIn: 'root'
})
export class AddressService {
  private apiUrl = `${environment.apiUrl}/api/addresses`;
  private locationsUrl = `${environment.apiUrl}/api/locations`;

  constructor(private http: HttpClient) {}

  getMyAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(this.apiUrl);
  }

  getAddressById(id: number): Observable<Address> {
    return this.http.get<Address>(`${this.apiUrl}/${id}`);
  }

  getDefaultAddress(): Observable<Address> {
    return this.http.get<Address>(`${this.apiUrl}/default`);
  }

  createAddress(address: Address): Observable<Address> {
    return this.http.post<Address>(this.apiUrl, address);
  }

  updateAddress(id: number, address: Address): Observable<Address> {
    return this.http.put<Address>(`${this.apiUrl}/${id}`, address);
  }

  setDefaultAddress(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/default`, {});
  }

  deleteAddress(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Ubicaciones
  getCities(): Observable<City[]> {
    return this.http.get<City[]>(`${this.locationsUrl}/cities`);
  }

  getDistricts(): Observable<District[]> {
    return this.http.get<District[]>(`${this.locationsUrl}/districts`);
  }

  getDistrictsByCity(cityId: number): Observable<District[]> {
    return this.http.get<District[]>(`${this.locationsUrl}/cities/${cityId}/districts`);
  }
}
```

### 3. Order Service

```typescript
// src/app/services/order.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface OrderItem {
  dishId: number;
  quantity: number;
  notes?: string;
}

export interface CreateOrderRequest {
  addressId: number;
  items: OrderItem[];
  paymentMethod: 'CASH' | 'CARD' | 'MERCADOPAGO';
  deliveryNotes?: string;
}

export interface Order {
  id: number;
  orderNumber: string;
  userId: number;
  addressId: number;
  subtotal: number;
  deliveryFee: number;
  total: number;
  status: string;
  paymentMethod: string;
  paymentStatus: string;
  paymentTransactionId?: string;
  items: OrderItemDetail[];
  address: any;
  estimatedDeliveryTime?: string;
  createdAt: string;
  updatedAt: string;
}

export interface OrderItemDetail {
  id: number;
  dishId: number;
  dishName: string;
  quantity: number;
  unitPrice: number;
  subtotal: number;
  notes?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/api/orders`;

  constructor(private http: HttpClient) {}

  createOrder(order: CreateOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, order);
  }

  getMyOrders(page: number = 0, size: number = 10): Observable<PageResponse<Order>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', 'createdAt,desc');
    return this.http.get<PageResponse<Order>>(this.apiUrl, { params });
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  getOrderByNumber(orderNumber: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderNumber}`);
  }

  getOrdersByStatus(status: string, page: number = 0, size: number = 10): Observable<PageResponse<Order>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Order>>(`${this.apiUrl}/status/${status}`, { params });
  }

  cancelOrder(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/cancel`, {});
  }
}
```

### 4. Cart Service (Local)

```typescript
// src/app/services/cart.service.ts
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface CartItem {
  dishId: number;
  name: string;
  price: number;
  imageUrl: string;
  quantity: number;
  notes?: string;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly STORAGE_KEY = 'shopping_cart';
  private cartItems = new BehaviorSubject<CartItem[]>([]);
  public cart$ = this.cartItems.asObservable();

  constructor() {
    this.loadCart();
  }

  addToCart(item: Omit<CartItem, 'quantity'>, quantity: number = 1): void {
    const currentCart = this.cartItems.value;
    const existingItem = currentCart.find(i => i.dishId === item.dishId);

    if (existingItem) {
      existingItem.quantity += quantity;
    } else {
      currentCart.push({ ...item, quantity });
    }

    this.updateCart(currentCart);
  }

  removeFromCart(dishId: number): void {
    const currentCart = this.cartItems.value.filter(item => item.dishId !== dishId);
    this.updateCart(currentCart);
  }

  updateQuantity(dishId: number, quantity: number): void {
    const currentCart = this.cartItems.value;
    const item = currentCart.find(i => i.dishId === dishId);

    if (item) {
      if (quantity <= 0) {
        this.removeFromCart(dishId);
      } else {
        item.quantity = quantity;
        this.updateCart(currentCart);
      }
    }
  }

  updateNotes(dishId: number, notes: string): void {
    const currentCart = this.cartItems.value;
    const item = currentCart.find(i => i.dishId === dishId);

    if (item) {
      item.notes = notes;
      this.updateCart(currentCart);
    }
  }

  getCart(): CartItem[] {
    return this.cartItems.value;
  }

  getTotal(): number {
    return this.cartItems.value.reduce((total, item) => total + (item.price * item.quantity), 0);
  }

  getItemCount(): number {
    return this.cartItems.value.reduce((count, item) => count + item.quantity, 0);
  }

  clearCart(): void {
    this.updateCart([]);
  }

  private loadCart(): void {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    if (stored) {
      try {
        const items = JSON.parse(stored);
        this.cartItems.next(items);
      } catch (e) {
        console.error('Error loading cart:', e);
        this.clearCart();
      }
    }
  }

  private updateCart(items: CartItem[]): void {
    this.cartItems.next(items);
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(items));
  }
}
```

### 5. Payment Service (MercadoPago)

```typescript
// src/app/services/payment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface PaymentPreferenceResponse {
  preferenceId: string;
  initPoint: string;
  sandboxInitPoint: string;
  orderId: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private apiUrl = `${environment.apiUrl}/api/payments`;

  constructor(private http: HttpClient) {}

  getPublicKey(): Observable<{ publicKey: string }> {
    return this.http.get<{ publicKey: string }>(`${this.apiUrl}/public-key`);
  }

  createPaymentPreference(orderId: number): Observable<PaymentPreferenceResponse> {
    const params = new HttpParams().set('orderId', orderId.toString());
    return this.http.post<PaymentPreferenceResponse>(`${this.apiUrl}/create-preference`, {}, { params });
  }

  testConfiguration(): Observable<any> {
    return this.http.get(`${this.apiUrl}/test`);
  }
}
```

---

## 🛒 Flujo de Carrito

### 1. Agregar al Carrito

```typescript
// menu.component.ts
export class MenuComponent implements OnInit {
  dishes: Dish[] = [];

  constructor(
    private dishService: DishService,
    private cartService: CartService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadDishes();
  }

  loadDishes(): void {
    this.dishService.getAllDishes().subscribe(dishes => {
      this.dishes = dishes;
    });
  }

  addToCart(dish: Dish): void {
    this.cartService.addToCart({
      dishId: dish.id,
      name: dish.name,
      price: dish.price,
      imageUrl: dish.imageUrl
    }, 1);

    this.snackBar.open(`${dish.name} agregado al carrito`, 'OK', {
      duration: 2000
    });
  }
}
```

### 2. Cart Modal Component

```typescript
// cart-modal.component.ts
export class CartModalComponent implements OnInit {
  cartItems: CartItem[] = [];
  total: number = 0;

  constructor(
    public dialogRef: MatDialogRef<CartModalComponent>,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cartService.cart$.subscribe(items => {
      this.cartItems = items;
      this.total = this.cartService.getTotal();
    });
  }

  updateQuantity(dishId: number, newQuantity: number): void {
    this.cartService.updateQuantity(dishId, newQuantity);
  }

  removeItem(dishId: number): void {
    this.cartService.removeFromCart(dishId);
  }

  proceedToCheckout(): void {
    this.dialogRef.close();
    this.router.navigate(['/checkout']);
  }

  close(): void {
    this.dialogRef.close();
  }
}
```

---

## 💳 Flujo de Checkout

### 1. Checkout Component

```typescript
// checkout.component.ts
export class CheckoutComponent implements OnInit {
  cartItems: CartItem[] = [];
  addresses: Address[] = [];
  selectedAddressId: number | null = null;
  total: number = 0;
  deliveryFee: number = 5.00;
  paymentMethod: 'CASH' | 'CARD' | 'MERCADOPAGO' = 'MERCADOPAGO';
  deliveryNotes: string = '';
  isProcessing: boolean = false;

  constructor(
    private cartService: CartService,
    private addressService: AddressService,
    private orderService: OrderService,
    private paymentService: PaymentService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadCart();
    this.loadAddresses();
  }

  loadCart(): void {
    this.cartItems = this.cartService.getCart();
    this.total = this.cartService.getTotal() + this.deliveryFee;
  }

  loadAddresses(): void {
    this.addressService.getMyAddresses().subscribe(addresses => {
      this.addresses = addresses;
      // Seleccionar dirección por defecto
      const defaultAddress = addresses.find(a => a.isDefault);
      if (defaultAddress) {
        this.selectedAddressId = defaultAddress.id!;
      }
    });
  }

  placeOrder(): void {
    if (!this.selectedAddressId) {
      this.snackBar.open('Por favor selecciona una dirección', 'OK', { duration: 3000 });
      return;
    }

    if (this.cartItems.length === 0) {
      this.snackBar.open('El carrito está vacío', 'OK', { duration: 3000 });
      return;
    }

    this.isProcessing = true;

    const orderRequest: CreateOrderRequest = {
      addressId: this.selectedAddressId,
      items: this.cartItems.map(item => ({
        dishId: item.dishId,
        quantity: item.quantity,
        notes: item.notes
      })),
      paymentMethod: this.paymentMethod,
      deliveryNotes: this.deliveryNotes
    };

    this.orderService.createOrder(orderRequest).subscribe({
      next: (order) => {
        console.log('Pedido creado:', order);

        if (this.paymentMethod === 'MERCADOPAGO') {
          this.initiateMercadoPagoPayment(order.id);
        } else {
          this.handleOrderSuccess(order);
        }
      },
      error: (error) => {
        console.error('Error al crear pedido:', error);
        this.snackBar.open('Error al crear el pedido', 'OK', { duration: 3000 });
        this.isProcessing = false;
      }
    });
  }

  private initiateMercadoPagoPayment(orderId: number): void {
    this.paymentService.createPaymentPreference(orderId).subscribe({
      next: (response) => {
        console.log('Preferencia de pago creada:', response);
        // Redirigir a MercadoPago
        window.location.href = response.sandboxInitPoint; // En desarrollo
        // window.location.href = response.initPoint; // En producción
      },
      error: (error) => {
        console.error('Error al crear preferencia de pago:', error);
        this.snackBar.open('Error al procesar el pago', 'OK', { duration: 3000 });
        this.isProcessing = false;
      }
    });
  }

  private handleOrderSuccess(order: Order): void {
    this.cartService.clearCart();
    this.snackBar.open('¡Pedido realizado con éxito!', 'OK', { duration: 3000 });
    this.router.navigate(['/orders', order.id]);
  }
}
```

---

## 🔄 Integración MercadoPago

### Flujo Completo

```
1. Usuario crea pedido → Order creado (PENDING)
2. Frontend llama /api/payments/create-preference
3. Backend crea preferencia en MercadoPago
4. Frontend redirige a MercadoPago checkout
5. Usuario paga en MercadoPago
6. MercadoPago redirige a success/failure/pending
7. MercadoPago notifica al webhook backend
8. Backend actualiza Order automáticamente
9. Backend envía email de confirmación
10. Usuario ve pedido actualizado
```

### Estados de Pago

| Estado MP | paymentStatus | orderStatus | Email |
|-----------|---------------|-------------|-------|
| `approved` | `APPROVED` | `CONFIRMED` | ✅ Enviado |
| `pending` | `PROCESSING` | `PENDING` | ✅ Enviado |
| `rejected` | `REJECTED` | `CANCELLED` | ✅ Enviado |
| `cancelled` | `REJECTED` | `CANCELLED` | ✅ Enviado |
| `refunded` | `REFUNDED` | `CANCELLED` | ✅ Enviado |

### Configuración en Angular

```typescript
// environment.ts
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080',
  mercadoPago: {
    publicKey: 'TEST-85e01ca4-16a5-4f4b-9335-90dda2c3de08'
  }
};

// environment.prod.ts
export const environment = {
  production: true,
  apiUrl: 'https://api.tu-dominio.com',
  mercadoPago: {
    publicKey: 'APP-XXXXXXXXXXXXXXXXXX' // Clave de producción
  }
};
```

### Páginas de Callback

```typescript
// payment-success.component.ts
export class PaymentSuccessComponent implements OnInit {
  orderNumber: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    // MercadoPago redirige con query params
    this.route.queryParams.subscribe(params => {
      const paymentId = params['payment_id'];
      const status = params['status'];
      const preferenceId = params['preference_id'];

      console.log('Payment success:', { paymentId, status, preferenceId });

      // El webhook ya actualizó el pedido, solo mostrar mensaje
      this.orderNumber = params['external_reference'] || '';
    });
  }

  goToOrders(): void {
    this.router.navigate(['/orders']);
  }
}
```

---

## 🔔 Webhooks

### Configuración del Webhook en MercadoPago

1. Ir a https://www.mercadopago.com.pe/developers/panel/webhooks
2. Agregar URL: `https://tu-backend.com/api/payments/webhook`
3. Seleccionar eventos: `payment`
4. Guardar

### Procesamiento Automático

```java
// Backend: PaymentController.java
@PostMapping("/webhook")
public ResponseEntity<String> handleWebhook(
    @RequestParam("type") String type,
    @RequestParam("data.id") String paymentId
) {
    logger.info("Webhook recibido: type={}, paymentId={}", type, paymentId);

    if ("payment".equals(type)) {
        mercadoPagoService.processWebhookNotification(type, paymentId);
        return ResponseEntity.ok("Webhook procesado");
    }

    return ResponseEntity.ok("Tipo de evento no soportado");
}
```

### Testing del Webhook Localmente

```bash
# Usar ngrok para exponer localhost
ngrok http 8080

# Configurar webhook en MercadoPago con URL de ngrok:
# https://xxxx-xx-xxx-xxx-xxx.ngrok.io/api/payments/webhook

# Probar pago en sandbox y ver logs
```

---

## 📝 Ejemplos Completos

### Flujo Usuario Completo (Frontend)

```typescript
// app.component.ts
export class AppComponent implements OnInit {
  isLoggedIn: boolean = false;
  cartCount: number = 0;

  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Verificar autenticación
    this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = !!user;
    });

    // Observar cambios en el carrito
    this.cartService.cart$.subscribe(items => {
      this.cartCount = items.reduce((count, item) => count + item.quantity, 0);
    });
  }

  openCart(): void {
    // Abrir modal del carrito
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
```

### HTTP Interceptor para JWT

```typescript
// jwt.interceptor.ts
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();

    if (token) {
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req);
  }
}

// app.module.ts
import { HTTP_INTERCEPTORS } from '@angular/common/http';

@NgModule({
  // ...
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ]
})
export class AppModule { }
```

### Guard para Rutas Protegidas

```typescript
// auth.guard.ts
import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    if (!this.authService.isLoggedIn()) {
      this.router.navigate(['/login']);
      return false;
    }

    // Verificar si requiere admin
    if (route.data['requiresAdmin'] && !this.authService.isAdmin()) {
      this.router.navigate(['/']);
      return false;
    }

    return true;
  }
}

// app-routing.module.ts
const routes: Routes = [
  { path: 'checkout', component: CheckoutComponent, canActivate: [AuthGuard] },
  { path: 'orders', component: OrdersComponent, canActivate: [AuthGuard] },
  { 
    path: 'admin', 
    component: AdminComponent, 
    canActivate: [AuthGuard],
    data: { requiresAdmin: true }
  }
];
```

---

## 🧪 Testing

### Test de Integración

```typescript
// checkout.component.spec.ts
describe('CheckoutComponent', () => {
  let component: CheckoutComponent;
  let fixture: ComponentFixture<CheckoutComponent>;
  let orderService: jasmine.SpyObj<OrderService>;
  let paymentService: jasmine.SpyObj<PaymentService>;

  beforeEach(() => {
    const orderServiceSpy = jasmine.createSpyObj('OrderService', ['createOrder']);
    const paymentServiceSpy = jasmine.createSpyObj('PaymentService', ['createPaymentPreference']);

    TestBed.configureTestingModule({
      declarations: [ CheckoutComponent ],
      providers: [
        { provide: OrderService, useValue: orderServiceSpy },
        { provide: PaymentService, useValue: paymentServiceSpy }
      ]
    });

    fixture = TestBed.createComponent(CheckoutComponent);
    component = fixture.componentInstance;
    orderService = TestBed.inject(OrderService) as jasmine.SpyObj<OrderService>;
    paymentService = TestBed.inject(PaymentService) as jasmine.SpyObj<PaymentService>;
  });

  it('should create order and initiate payment', () => {
    const mockOrder: Order = { id: 1, orderNumber: 'ORD-123', /* ... */ };
    const mockPayment: PaymentPreferenceResponse = { 
      preferenceId: 'pref-123',
      initPoint: 'https://mercadopago.com/checkout',
      sandboxInitPoint: 'https://sandbox.mercadopago.com/checkout',
      orderId: 1,
      status: 'created'
    };

    orderService.createOrder.and.returnValue(of(mockOrder));
    paymentService.createPaymentPreference.and.returnValue(of(mockPayment));

    component.placeOrder();

    expect(orderService.createOrder).toHaveBeenCalled();
    expect(paymentService.createPaymentPreference).toHaveBeenCalledWith(1);
  });
});
```

---

## 📚 Recursos Adicionales

- **MercadoPago Docs**: https://www.mercadopago.com.pe/developers/es/docs
- **Angular HttpClient**: https://angular.io/guide/http
- **RxJS Operators**: https://rxjs.dev/api
- **Spring Boot REST**: https://spring.io/guides/gs/rest-service/

---

<p align="center">
  <b>🎨 Guía de Integración - El Pollo Empoderado</b><br>
  Última actualización: 11 de diciembre de 2025
</p>
