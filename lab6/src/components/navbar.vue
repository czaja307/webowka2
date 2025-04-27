<script setup lang="ts">

import { Button } from "@/components/ui/button";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet";
import { HamburgerMenuIcon } from "@radix-icons/vue";

</script>

<template>
  <div class="">
    <nav class="w-full flex bg-gray-300 p-4">
      <div class="flex items-center flex-row gap-10 w-full">
        <router-link to="/" class="font-bold text-lg sm:text-xl text-gray-600">Bookstore Admin</router-link>
        <div class="hidden sm:flex pl-10">
          <Button variant="ghost" class="" as-child>
            <router-link to="/books">Books</router-link>
          </Button>
          <Button variant="ghost" class="" as-child>
            <router-link to="/authors">Authors</router-link>
          </Button>
          <Button variant="ghost" class="" as-child>
            <router-link to="/readers">Readers</router-link>
          </Button>
          <Button variant="ghost" class="" as-child>
            <router-link to="/rentals">Rentals</router-link>
          </Button>
        </div>
        <div class="sm:hidden ml-auto">
          <Sheet>
            <SheetTrigger >
              <HamburgerMenuIcon />
            </SheetTrigger>
            <SheetContent>
              <div class="flex flex-col pt-8 h-full items-center gap-10">
              <Button variant="ghost" class="text-xl" as-child>
                <router-link to="/books">Books</router-link>
              </Button>
              <Button variant="ghost" class="text-xl" as-child>
                <router-link to="/authors">Authors</router-link>
              </Button>
              <Button variant="ghost" class="text-xl" as-child>
                <router-link to="/readers">Readers</router-link>
              </Button>
              <Button variant="ghost" class="text-xl" as-child>
                <router-link to="/rentals">Rentals</router-link>
              </Button>
              </div>
            </SheetContent>
          </Sheet>
        </div>

      </div>
    </nav>
  </div>

</template>

<style scoped>

</style>