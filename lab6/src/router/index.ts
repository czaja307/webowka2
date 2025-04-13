import {createRouter, createWebHistory} from "vue-router";
import MainLayout from "@/layouts/main-layout.vue";
import EmptyLayout from "@/layouts/empty-layout.vue";
import HomePage from "@/views/home-page.vue";
import BookPage from "@/views/book-page.vue";
import AuthorPage from "@/views/author-page.vue";
import ReaderPage from "@/views/reader-page.vue";
import RentalPage from "@/views/rental-page.vue";

const router = createRouter({
        history: createWebHistory(import.meta.env.BASE_URL),
        routes: [
            {
                path: "/",
                name: "home",
                component: HomePage,
                meta: {
                    layout: EmptyLayout,
                },
            },
            {
                path: "/books",
                name: "books",
                component: BookPage,
                meta: {
                    layout: MainLayout,
                },
            },
            {
                path: "/authors",
                name: "authors",
                component: AuthorPage,
                meta: {
                    layout: MainLayout,
                },
            },
            {
                path: "/readers",
                name: "readers",
                component: ReaderPage,
                meta: {
                    layout: MainLayout,
                },
            },
            {
                path: "/rentals",
                name: "rentals",
                component: RentalPage,
                meta: {
                    layout: MainLayout,
                },
            }
        ],
    }
)

export default router;