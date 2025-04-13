<script setup lang="ts">
import {
  Pagination,
  PaginationEllipsis,
  PaginationFirst,
  PaginationLast,
  PaginationList,
  PaginationListItem,
  PaginationNext,
  PaginationPrev,
} from '@/components/ui/pagination'
import {computed, ref} from 'vue'
import { useQuery, useMutation, useQueryClient } from "@tanstack/vue-query";

interface Book {
  id?: string
  title: string
  isAvailable?: boolean
  author?: Author | null
  authorId: string
  pages: number
}

interface Author {
  id: string
  firstName: string
  lastName: string
  birthDate: string | Date
}

interface BookResponse {
  content: Book[]
  totalPages: number
}



const queryClient = useQueryClient();
const queryKey = computed(() => {
  return ['books', page.value, itemsPerPage.value];
});
const page = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(1);
const editingId = ref(null);
const currentBook = ref<Book>({
  id: '',
  title: '',
  isAvailable: false,
  author: null,
  authorId: 0,
});

const authorList = ref<Author[]>([]);
const bookList = ref<Book[]>([]);

const fetchBooks = async ({ queryKey }: { queryKey: (string | number)[]
}) => {
  const [key, page, itemsPerPage] = queryKey;
  console.log(key, page, itemsPerPage, queryKey)
  const response = await fetch(`http://localhost:8080/api/v1/books?page=${page}&size=${itemsPerPage}`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as BookResponse;
  totalPages.value = data.totalPages;
  return data;
};

const { data, error, isLoading, isError, isFetching } = useQuery({
  queryKey: queryKey.value,
  queryFn: fetchBooks,
  keepPreviousData: true,
})

</script>


<template>
  <div class="flex items-center justify-center h-screen p-10">


    <Pagination v-slot="{ page }" :items-per-page="10" :total="100" :sibling-count="1" show-edges :default-page="2">
      <PaginationList v-slot="{ items }" class="flex items-center gap-1">
        <PaginationFirst />
        <PaginationPrev />

        <template v-for="(item, index) in items">
          <PaginationListItem v-if="item.type === 'page'" :key="index" :value="item.value" as-child>
            <Button class="w-9 h-9 p-0" :variant="item.value === page ? 'default' : 'outline'">
              {{ item.value }}
            </Button>
          </PaginationListItem>
          <PaginationEllipsis v-else :key="item.type" :index="index" />
        </template>

        <PaginationNext />
        <PaginationLast />
      </PaginationList>
    </Pagination>

  </div>

</template>

<style scoped>

</style>