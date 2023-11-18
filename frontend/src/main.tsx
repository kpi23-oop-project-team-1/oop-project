import React from "react";
import ReactDOM from "react-dom/client";
import { createBrowserRouter, RouterProvider } from "react-router-dom";

import { DiContainerContext, TestDiContainer } from "./diContainer";
import { StringResourcesContext } from "./StringResourcesContext";
import { ukrainianStringResources } from "./StringResources";

import "./styles/GeneralStyles.scss";
import MainPage from "./pages/MainPage";
import ProductsPage from "./pages/ProductsPage";
import SignInPage from "./pages/SignInPage";
import SignUpPage from "./pages/SignUpPage";
import ProductInfoPage from "./pages/ProductInfoPage";

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
    }
  ]
);

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
    <React.StrictMode>
        <StringResourcesContext.Provider value={ukrainianStringResources}>
            <DiContainerContext.Provider value={TestDiContainer}>
                <RouterProvider router={router} />
            </DiContainerContext.Provider>
        </StringResourcesContext.Provider>
    </React.StrictMode>
);
