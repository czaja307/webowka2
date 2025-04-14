<script setup lang="ts">
import {computed, ref} from 'vue'
import {useMutation, useQuery, useQueryClient} from "@tanstack/vue-query";
import {Button} from "@/components/ui/button";
import {ArrowLeftIcon, ArrowRightIcon} from "@radix-icons/vue";
import {Input} from "@/components/ui/input";
import {Card, CardContent} from "@/components/ui/card";
import {Checkbox} from "@/components/ui/checkbox";

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

interface AuthorResponse {
  content: Author[]
  totalPages: number
}



const queryClient = useQueryClient();

const page = ref(0);
const totalPages = ref(1);
const itemsPerPage = ref(3);
const editingId = ref(null);
const currentBook = ref<Book>({
  id: '',
  title: '',
  isAvailable: false,
  author: null,
  authorId: 0,
  pages: 0,
});

const queryKey = computed(() => {
  return ['books', page.value, itemsPerPage.value];
});

const fetchBooks = async ({ queryKey }: { queryKey: (string | number)[]
}) => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/books?page=${page.value}&size=${itemsPerPage.value}`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  const data = await response.json() as BookResponse;
  totalPages.value = data.totalPages;
  console.log(totalPages.value)
  return data;
};

const {data, error, isLoading, isError} = useQuery({
  queryKey: queryKey.value,
  queryFn: fetchBooks,
  keepPreviousData: true,
})

const books = computed(() => data.value ? data.value.content : [])

const fetchAuthors = async () => {
  const response = await fetch(`${import.meta.env.VITE_API_URL}/authors`);
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return await response.json() as Promise<AuthorResponse>;
};

const {data: authorsData, error: authorsError, isLoading: authorsLoading} = useQuery({
  queryKey: ['authors'],
  queryFn: fetchAuthors,
  keepPreviousData: true,
})

const authors = computed(() => authorsData.value ? authorsData.value.content : [])

const deleteMutation = useMutation({
  mutationFn: async (id: string) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/books/${id}`, {
      method: 'DELETE',
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return id;
  },
  onSuccess: (id) => {
    queryClient.invalidateQueries({queryKey: ['books']});
  },
});

const deleteBook = async (id: string) => {
  await deleteMutation.mutateAsync(id);
};

const updateMutation = useMutation({
  mutationFn: async ({id, updatedData}: { id: string, updatedData: Partial<Book> }) => {
    const response = await fetch(`${import.meta.env.VITE_API_URL}/books/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(updatedData),
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    return response.json() as Promise<Book>;
  },
  onSuccess: () => {
    queryClient.invalidateQueries({queryKey: ['books']});
  },
});

const nextPage = () => {
  if (page.value < totalPages.value - 1) {
    page.value++;
  }
  queryClient.invalidateQueries({queryKey: ['books']});
};

const prevPage = () => {
  if (page.value > 0) {
    page.value--;
  }
  queryClient.invalidateQueries({queryKey: ['books']});
};

const startEditing = (book: Book) => {
  editingId.value = book.id;
  currentBook.value = {
    id: book.id,
    title: book.title,
    isAvailable: book.isAvailable,
    authorId: book.authorId,
    pages: book.pages,
  };
};

const saveBook = async () => {
  if (editingId.value) {
    console.log("book_c", currentBook)
    await updateMutation.mutateAsync(
        {
          id: editingId.value,
          updatedData: {
            title: currentBook.value.title,
            authorId: currentBook.value.authorId,
            pages: currentBook.value.pages,
          }
        });
    editingId.value = null;
    // currentBook.value = null;
  }
};

const cancelEditing = () => {
  editingId.value = null;
  currentBook.value = null;
};

</script>


<template>
  <div class="flex items-center h-screen flex-col gap-10 p-4">
    <div v-if="isLoading || authorsLoading">
      Loading...
    </div>

    <div v-else-if="isError || authorsError">
      Error: {{ error.message }} {{ authorsError.message }}
    </div>

    <div v-else>
      <ul>
        <li v-for="book in books" :key="book.id">
          <div class="p-10">
            <Card class="sm:p-4 p-1">
              <CardContent class="flex flex-col gap-4">
                <div class="justify-start flex flex-col">
                  <label for="title" class="text-sm text-gray-600">Title</label>
                  <Input
                      v-if="editingId !== book.id"
                      v-model="book.title"
                      type="text"
                      id="title"
                      :disabled="editingId !== book.id"
                  />
                  <Input
                      v-if="editingId === book.id"
                      v-model="currentBook.title"
                      type="text"
                      id="title"
                      :disabled="editingId !== book.id"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="isAvailable" class="text-sm text-gray-600">Is available</label>
                  <Checkbox
                      v-model="book.isAvailable"
                      id="isAvailable"
                      :disabled="true"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="pages" class="text-sm text-gray-600">Pages</label>
                  <Input
                      v-if="editingId !== book.id"
                      v-model="book.pages"
                      type="number"
                      id="pages"
                      :disabled="editingId !== book.id"
                  />
                  <Input
                      v-if="editingId === book.id"
                      v-model="currentBook.pages"
                      type="number"
                      id="pages"
                      :disabled="editingId !== book.id"
                  />
                </div>
                <div class="justify-start flex flex-col">
                  <label for="title" class="text-sm text-gray-600">Author</label>
                  <div v-if="editingId === book.id">
                    <select
                        v-model="currentBook.authorId"
                        id="authorId"
                        :disabled="editingId !== book.id"
                        class="border border-gray-300 rounded-md p-2"
                    >
                      <option disabled value="">Select author</option>
                      <option v-for="author in authors" :key="author.id" :value="author.id">
                        {{ author.firstName }} {{ author.lastName }}
                      </option>
                    </select>
                  </div>
                  <div v-else>
                    {{ book.author?.firstName }} {{ book.author?.lastName }}
                  </div>
                  <Button
                      v-if="editingId !== book.id"
                      class="mt-2"
                      @click="startEditing(book)"
                      >
                    Edit
                  </Button>
                  <Button
                      v-if="editingId !== book.id"
                      class="mt-2"
                      @click="deleteBook(book.id)"
                      variant="destructive"
                  >Delete</Button>
                  <Button
                      v-if="editingId === book.id"
                      class="mt-2"
                      @click="saveBook"
                      >
                    Save
                  </Button>
                  <Button
                      v-if="editingId === book.id"
                      variant="outline"
                      class="mt-2"
                      @click="cancelEditing"
                      >
                    Cancel
                  </Button>
                </div>
              </CardContent>
            </Card>
          </div>
        </li>
      </ul>

    </div>

    <div class="flex flex-row gap-4 items-center p-4">
      <Button variant="outline" @click="prevPage">
        <ArrowLeftIcon/>
      </Button>
      <p>
        {{ page + 1 }}
      </p>
      <Button variant="outline" @click="nextPage">
        <ArrowRightIcon/>
      </Button>
    </div>


    <!--    <Pagination v-slot="{ page }" :items-per-page="itemsPerPage.value" :total="totalPages.value" :sibling-count="1" show-edges :default-page="0">-->
    <!--      <PaginationList v-slot="{ items }" class="flex items-center gap-1">-->
    <!--        <PaginationFirst @click="nextPage" />-->
    <!--        <PaginationPrev @click="prevPage"/>-->

    <!--        <template v-for="(item, index) in items">-->
    <!--          <PaginationListItem v-if="item.type === 'page'" :key="index" :value="item.value" as-child>-->
    <!--            <Button class="w-9 h-9 p-0" :variant="item.value === page ? 'default' : 'outline'">-->
    <!--              {{ item.value }}-->
    <!--            </Button>-->
    <!--          </PaginationListItem>-->
    <!--          <PaginationEllipsis v-else :key="item.type" :index="index" />-->
    <!--        </template>-->

    <!--        <PaginationNext />-->
    <!--        <PaginationLast />-->
    <!--      </PaginationList>-->
    <!--    </Pagination>-->

  </div>

</template>

<style scoped>

</style>