<script setup lang="ts">

import {Card, CardContent, CardDescription, CardHeader, CardTitle} from "@/components/ui/card";
import {Button} from "@/components/ui/button";
</script>

<template>
  <div class="flex items-center justify-center h-screen px-10">
    <Card class="w-full max-w-md">
      <CardHeader>
        <CardTitle>Welcome to Bookstore Admin panel</CardTitle>
        <CardDescription>Here you can manage our resources</CardDescription>
      </CardHeader>
      <CardContent>
        <Button class="mt-4" as-child>
          <RouterLink to="/books">Start</RouterLink>
        </Button>
      </CardContent>
    </Card>
  </div>

</template>

<style scoped>

</style>