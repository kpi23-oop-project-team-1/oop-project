import React from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { AppDiContainer, DiContainerContext } from "./diContainer";
import { StringResourcesContext } from "./StringResourcesContext";
import { ukrainianStringResources } from "./StringResources";

import "./styles/GeneralStyles.scss";
import MainPage from "./pages/MainPage";
import ProductsPage from "./pages/ProductsPage";
import SignInPage from "./pages/SignInPage";
import SignUpPage from "./pages/SignUpPage";
import ProductInfoPage from "./pages/ProductInfoPage";
import AddProductPage from "./pages/AddProductPage";
import MyProductsPage from "./pages/MyProductsPage";
import MyAccountPage from "./pages/MyAccountPage";
import EditProductPage from "./pages/EditProductPage";
import UserPage from "./pages/UserPage";
import AdminPage from "./pages/AdminPage";

const router = createBrowserRouter([
    {
      path: "/",
      element: <MainPage/>
    },
    {
        path: "/signin",
        element: <SignInPage/>
    },
    {
        path: "/signup",
        element: <SignUpPage/>
    },
    {
        path: "/products",
        element: <ProductsPage/>
    },
    {
        path: "/product/:productId/",
        element: <ProductInfoPage/>
    },
    {
        path: "/addproduct",
        element: <AddProductPage/>
    },
    {
        path: "/editproduct/:productId/",
        element: <EditProductPage/>,
    },
    {
        path: "/myproducts",
        element: <MyProductsPage/>
    },
    {
        path: "/myaccount",
        element: <MyAccountPage/>
    },
    {
        path: "/user/:userId/",
        element: <UserPage/>
    },
    {
        path: "/admin",
        element: <AdminPage/>
    }
  ]
);

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
    <React.StrictMode>
        <StringResourcesContext.Provider value={ukrainianStringResources}>
            <DiContainerContext.Provider value={AppDiContainer}>
                <RouterProvider router={router} />
            </DiContainerContext.Provider>
        </StringResourcesContext.Provider>
    </React.StrictMode>
);
